package com.rhetorical.town.towns;

import com.rhetorical.town.TheNewTown;
import com.rhetorical.town.files.TownFile;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import sun.reflect.generics.tree.Tree;

import java.util.*;

public class TownManager {
	private static TownManager instance;

	private Map<String, Town> towns = new HashMap<>();

	private int upkeepPeriod;
	private int taxPeriod;
	private int checkupPeriod;

	private TownManager() {
		if (instance != null)
			return;

		instance = this;

		upkeepPeriod = TheNewTown.getInstance().getConfig().getInt("period.upkeep");
		taxPeriod = TheNewTown.getInstance().getConfig().getInt("period.tax");
		checkupPeriod = TheNewTown.getInstance().getConfig().getInt("period.checkup");

		load();

		startCheckups();
	}

	public static TownManager getInstance() {
		return instance != null ? instance : new TownManager();
	}

	private void load() {
		TownFile file = TownFile.open();
		ConfigurationSection townNames = file.getData().getConfigurationSection("");

		if (townNames != null)
			for (String name : townNames.getKeys(false)) {
				Town town = new Town(name);
				towns.put(name, town);
			}

		for (Town town : getTowns().values())
			town.loadPlots(file);
	}

	public Map<String, Town> getTowns() {
		return towns;
	}

	public List<Town> getOrderedTowns() {
		List<Town> map = new ArrayList<>();
		List<Town> unsorted = new ArrayList<>(getTowns().values());


		for (int i = 0; i < getTowns().values().size(); i++) {
			Town lowest = null;
			for (Town t : unsorted) {
				if (lowest == null || t.getPlots().size() < lowest.getPlots().size())
					lowest = t;
			}
			if (lowest != null) {
				unsorted.remove(lowest);
				map.add(lowest);
			}
		}

		return map;
	}

	public Town getTown(String name) {
		if (towns.containsKey(name))
			return towns.get(name);
		return null;
	}

	public Town getTown(Chunk chunk) {
		for (Town town : towns.values()) {
			for (Plot plot : town.getPlots()) {
				if (plot.getX() == chunk.getX() && plot.getZ() == chunk.getZ() && plot.getWorldName().equals(chunk.getWorld().getName()))
					return town;
			}
		}

		return null;
	}

	public boolean isChunkClaimed(Chunk chunk) {
		for (Town town : getTowns().values())
			if (town.isChunkClaimed(chunk))
				return true;
		return false;
	}

	public void deleteTown(String name) {
		if (getTowns().containsKey(name)) {
			getTown(name).delete();
			getTowns().remove(name);
		}
		TownFile file = TownFile.open();
		file.getData().set(name, null);
		file.saveData();
	}

	public boolean createTown(UUID owner, Chunk chunk, String name) {
		if (isChunkClaimed(chunk))
			return false;
		
		Collection<String> townNames = new HashSet<>();
		for (String s : getTowns().keySet())
			townNames.add(s.toLowerCase());

		if (townNames.contains(name.toLowerCase()))
			return false;

		for (Town t : getTowns().values()) {
			if (t.getMayor().equals(owner) || t.getResidents().contains(owner))
				return false;
		}

		Town town;
		try {
			town = new Town(owner, chunk, name);
		} catch (PlotAlreadyExistsException e) {
			Bukkit.getLogger().info(String.format("A plot was to be claimed at [%s, %s], but was unable to do so!", e.getChunk().getX(), e.getChunk().getZ()));
			Bukkit.getLogger().info(String.format("Reason: %s", e.getFailReason().toString()));
			return false;
		}

		float cost = TheNewTown.getInstance().getCreationCost();

		EconomyResponse response = TheNewTown.getInstance().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(owner), cost);
		Player p = Bukkit.getPlayer(owner);
		if (!response.transactionSuccess()) {
			if (p != null)
				p.sendMessage(ChatColor.RED + String.format("Insufficient funds to create town! Funds required: %s", cost));
			return false;
		} else {
			if (p != null)
				p.sendMessage(ChatColor.GREEN + String.format("%s has been charged to your account!", cost));
		}

		town.save();

		getTowns().put(name, town);

		return true;
	}

	TownType getPreviousTownType(TownType current) {
		switch(current) {
			case CITY_STATE:
				return TownType.MAJOR_CITY;
			case MAJOR_CITY:
				return TownType.CITY;
			case CITY:
				return TownType.TOWN;
			case TOWN:
				return TownType.VILLAGE;
			case VILLAGE:
				return TownType.HAMLET;
			default:
				return TownType.HAMLET;
		}
	}

	TownType getNextTownType(TownType current) {
		switch(current) {
			case HAMLET:
				return TownType.VILLAGE;
			case VILLAGE:
				return TownType.TOWN;
			case TOWN:
				return TownType.CITY;
			case CITY:
				return TownType.MAJOR_CITY;
			case MAJOR_CITY:
				return TownType.CITY_STATE;
			default:
				return TownType.CITY_STATE;
		}
	}

	private void startCheckups() {
		BukkitRunnable br = new BukkitRunnable() {
			@Override
			public void run() {

				for (Town town : getTowns().values())
					town.collectTaxes();

				startCheckups();
			}
		};
		br.runTaskLater(TheNewTown.getInstance(), checkupPeriod * 1200);
	}

	public int getTaxablePlots(Town town) {
		int base = town.getTownType() == TownType.HAMLET ? 0 : getPreviousTownType(town.getTownType()).getMaxPlots();
		return town.getPlots().size() - base;
	}

	public float getUpkeep(Town town) {

		float p = (float) getTaxablePlots(town);

		return town.getTownType().getFlatCost() + (town.getTownType().getPlotCost() * p);
	}

	public Town getTownOfPlayer(UUID id) {
		for (Town town : getTowns().values()) {
			if (town.getResidents().contains(id))
				return town;
		}
		return null;
	}

	public int getUpkeepPeriod() {
		return upkeepPeriod;
	}

	public int getTaxPeriod() {
		return taxPeriod;
	}
}
