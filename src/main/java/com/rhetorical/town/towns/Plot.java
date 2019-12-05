package com.rhetorical.town.towns;

import com.rhetorical.town.TheNewTown;
import com.rhetorical.town.files.TownFile;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.HandlerList;

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


	Plot(long id, UUID owner, UUID leaser, Chunk chunk, boolean forSale, float cost, String town) {
		this.id = id;
		setOwner(owner);
		setLeaser(leaser);
		setChunk(chunk);
		setForSale(forSale);
		setCost(cost);
		setTown(town);

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

	boolean tryLeasePlot(UUID renter) {
		EconomyResponse response = TheNewTown.getInstance().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(renter), getCost());

		if (response.transactionSuccess()) {
			setLeaser(renter);
			EconomyResponse r = TheNewTown.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(getOwner()), getCost());
			return true;
		}

		return false;
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


		return new Plot(id, owner, leaser, chunk, fs, cost, town);
	}


}
