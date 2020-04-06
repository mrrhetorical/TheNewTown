package com.rhetorical.town.towns;

import com.rhetorical.town.TheNewTown;
import com.rhetorical.town.files.TownFile;
import com.rhetorical.town.towns.flags.TownFlag;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Plot {

	private final long id; // inconsequential

	private String town; // town owner

	private UUID owner, leaser;

	private String worldName;
	private int x, z;

	private boolean forSale;
	private float cost;

	private final PlotListener plotListener;

	private Map<TownFlag, Boolean> flags;


	Plot(long id, UUID owner, UUID leaser, Chunk chunk, boolean forSale, float cost, String town, Map<TownFlag, Boolean> flags) {
		this.id = id;
		setOwner(owner);
		setLeaser(leaser);
		setChunk(chunk);
		setForSale(forSale);
		setCost(cost);
		setTown(town);
		this.flags = flags;

		plotListener = new PlotListener(this);
		Bukkit.getPluginManager().registerEvents(plotListener, TheNewTown.getInstance());
	}

	private void setOwner(UUID value) {
		owner = value;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setLeaser(UUID value) {
		leaser = value;
	}

	public UUID getLeaser() {
		return leaser;
	}

	public String getWorldName() {
		return worldName;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public boolean isForSale() {
		return forSale;
	}

	public void setForSale(boolean value) {
		forSale = value;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float value) {
		cost = value;
	}

	public long getId() {
		return id;
	}

	public String getTown() {
		return town;
	}

	private void setTown(String value) {
		town = value;
	}

	public void setChunk(Chunk chunk) {
		worldName = chunk.getWorld().getName();
		x = chunk.getX();
		z = chunk.getZ();
	}

	public boolean isChunk(Chunk chunk) {
		return getX() == chunk.getX() && getZ() == chunk.getZ() && getWorldName().equalsIgnoreCase(chunk.getWorld().getName());
	}

	public boolean isInPlot(Location location) {
		if (location.getWorld() == null)
			return false;
		return getWorldName().equalsIgnoreCase(location.getWorld().getName()) && location.getChunk().getX() == getX() && location.getChunk().getZ() == getZ();
	}

	void unregister() {
		HandlerList.unregisterAll(plotListener);
	}

	void save(String town, TownFile file) {
		String base = town + ".plots." + getId();

		file.getData().set(base + ".world", getWorldName());
		file.getData().set(base + ".x", getX());
		file.getData().set(base + ".z", getZ());
		file.getData().set(base + ".leaser", getLeaser() != null ? getLeaser().toString() : "none");
		file.getData().set(base + ".forSale", isForSale());
		file.getData().set(base + ".cost", getCost());

		if (getFlags().isEmpty())
			file.getData().set(base + ".flags", null);
		else
			for (TownFlag flag : getFlags().keySet())
				file.getData().set(base + ".flags." + flag.toString().toLowerCase(), getFlags().get(flag));

		file.saveData();
	}

	double collectRent() {
		if (leaser == null)
			return 0;

		double collected = 0;

		EconomyResponse r = TheNewTown.getInstance().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(getLeaser()), getCost());

		if (!r.transactionSuccess()) {
			setLeaser(null);
		} else
			collected += getCost();

		if (!isForSale())
			setLeaser(null);

		return collected;
	}

	static Plot loadPlot(String town, long id, TownFile file) {

		String w = file.getData().getString(town + ".plots." + id + ".world");
		int x = file.getData().getInt(town + ".plots." + id + ".x");
		int z = file.getData().getInt(town + ".plots." + id + ".z");

		String l = file.getData().getString(town + ".plots." + id + ".leaser");

		UUID leaser = l != null && !l.equalsIgnoreCase("none") ? UUID.fromString(l) : null;

		UUID owner = TownManager.getInstance().getTown(town).getMayor();

		boolean fs = file.getData().getBoolean(town + ".plots." + id + ".forSale");
		float cost = (float) file.getData().getDouble(town + ".plots." + id + ".cost");

		World world = w != null ? Bukkit.getWorld(w) : null;

		if (world == null)
			return null;

		Chunk chunk = world.getChunkAt(x, z);

		Map<TownFlag, Boolean> townFlags = new HashMap<>();

		ConfigurationSection f = file.getData().getConfigurationSection(town + ".plots." + id + ".flags");
		if (f != null)
			for (String key : f.getKeys(false)) {
				TownFlag flag;
				try {
					flag = TownFlag.valueOf(key.toUpperCase());
				} catch (Exception ignored) { continue; }

				boolean v = file.getData().getBoolean(town  + ".plots." + id + ".flags." + key);
				townFlags.put(flag, v);
			}


		return new Plot(id, owner, leaser, chunk, fs, cost, town, townFlags);
	}

	public Map<TownFlag, Boolean> getFlags() {
		return flags;
	}

	public void setFlag(TownFlag flag, boolean value) {
		flags.put(flag, value);
	}

	public boolean hasFlag(TownFlag flag) {
		return getFlags().containsKey(flag);
	}

	public boolean getFlag(TownFlag flag) {
		if (getFlags().containsKey(flag))
			return getFlags().get(flag);

		return TownManager.getInstance().getTown(getTown()).getFlag(flag);
	}

	public boolean removeFlag(TownFlag flag) {
		return flags.remove(flag);
	}

	public void playBorderParticle(Town town, int height, Player p) {

		Particle particle = Particle.REDSTONE;

		boolean isSelfTown = town.getResidents().contains(p.getUniqueId());

		Particle.DustOptions dustOptions = new Particle.DustOptions(isSelfTown ? BorderManager.getInstance().getSelf().getColor() : BorderManager.getInstance().getNeutral().getColor(), 2f);

		int borderHeight = isSelfTown ? BorderManager.getInstance().getSelf().getHeight() : BorderManager.getInstance().getNeutral().getHeight();

		World world = Bukkit.getWorld(getWorldName());
		Chunk chunk = world.getChunkAt(getX(), getZ());

		//north

		if (town.getPlot(getWorldName(), getX(), getZ() + 1) == null) {
			for (int i = 0; i < 16; i++) {
				Location loc = chunk.getBlock(i, height, 15).getLocation();
				for (int h = 0; h < borderHeight; h++)
					p.spawnParticle(particle, loc.getX() + 0.5f, (height - (borderHeight >> 1)) + h, loc.getZ() + 1f, 1, dustOptions);
			}
		}

		//south

		if (town.getPlot(getWorldName(), getX(), getZ() - 1) == null) {
			for (int i = 0; i < 16; i++) {
				Location loc = chunk.getBlock(i, height, 0).getLocation();
				for (int h = 0; h < borderHeight; h++)
					p.spawnParticle(particle, loc.getX() + 0.5f, (height - (borderHeight >> 1)) + h, loc.getZ(), 1, dustOptions);
			}
		}

		//east

		if (town.getPlot(getWorldName(), getX() + 1, getZ()) == null) {
			for (int i = 0; i < 16; i++) {
				Location loc = chunk.getBlock(15, height, i).getLocation();
				for (int h = 0; h < borderHeight; h++)
					p.spawnParticle(particle, loc.getX() + 1f, (height - (borderHeight >> 1)) + h, loc.getZ() + 0.5f, 1, dustOptions);
			}
		}

		//west

		if (town.getPlot(getWorldName(), getX() - 1, getZ()) == null) {
			for (int i = 0; i < 16; i++) {
				Location loc = chunk.getBlock(0, height, i).getLocation();
				for (int h = 0; h < borderHeight; h++)
					p.spawnParticle(particle, loc.getX(), (height - (borderHeight >> 1)) + h, loc.getZ() + 0.5f, 1, dustOptions);
			}
		}
	}

}
