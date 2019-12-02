package com.rhetorical.town.towns;

import com.rhetorical.town.TheNewTown;
import com.rhetorical.town.files.TownFile;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class TownManager {
	private static TownManager instance;

	private Map<String, Town> towns = new HashMap<>();

	private int upkeepPeriod = 72;
	private int taxPeriod = 72;
	private int checkupPeriod = 5;

	private TownManager() {
		TownFile file = TownFile.open();
		ConfigurationSection townNames = file.getData().getConfigurationSection("");

		if (townNames != null)
			for (String name : townNames.getKeys(false)) {
				Town town = new Town(name);
				towns.put(name, town);
			}

		startCheckups();
	}

	public static TownManager getInstance() {
		if (instance == null)
			instance = new TownManager();

		return instance;
	}

	public Map<String, Town> getTowns() {
		return towns;
	}

	public Town getTown(String name) {
		if (towns.containsKey(name))
			return towns.get(name);
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

		//todo: worldguard stuff to check

		Town town;
		try {
			town = new Town(owner, chunk, name);
		} catch (PlotAlreadyExistsException e) {
			Bukkit.getLogger().info(String.format("A plot was to be claimed at [%s, %s], but was unable to do so!", e.getChunk().getX(), e.getChunk().getZ()));
			Bukkit.getLogger().info(String.format("Reason: %s", e.getFailReason().toString()));
			return false;
		}

		Collection<String> townNames = new HashSet<>();
		for (String s : getTowns().keySet())
			townNames.add(s.toLowerCase());

		if (townNames.contains(name.toLowerCase()))
			return false;

		for (Town t : getTowns().values()) {
			if (t.getMayor().equals(owner) || t.getResidents().contains(owner))
				return false;
		}

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

	public int getUpkeepPeriod() {
		return upkeepPeriod;
	}

	public int getTaxPeriod() {
		return taxPeriod;
	}
}
