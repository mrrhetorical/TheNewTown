package com.rhetorical.town.towns;

import com.rhetorical.town.files.TownFile;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Listener;

import java.util.UUID;

public class Plot implements Listener {

	private final long id; //inconsequential

	private UUID owner, leaser;

	private String worldName;
	private int x, z;

	private boolean forSale;
	private float cost;


	Plot(long id, UUID owner, UUID leaser, Chunk chunk, boolean forSale, float cost) {
		this.id = id;
		setOwner(owner);
		setLeaser(leaser);
		setChunk(chunk);
		setForSale(forSale);
		setCost(cost);
	}

	private void setOwner(UUID value) {
		owner = value;
	}

	public UUID getOwner() {
		return owner;
	}

	private void setLeaser(UUID value) {
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

	public void setChunk(Chunk chunk) {
		worldName = chunk.getWorld().getName();
		x = chunk.getX();
		z = chunk.getZ();
	}

	void save(String town, TownFile file) {
		String base = town + ".plots." + getId();

		file.getData().set(base + ".world", getWorldName());
		file.getData().set(base + ".x", getX());
		file.getData().set(base + ".z", getZ());
		file.getData().set(base + ".leaser", getLeaser());
		file.getData().set(base + ".forSale", isForSale());
		file.getData().set(base + ".cost", getCost());
		file.saveData();
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


		return new Plot(id, owner, leaser, chunk, fs, cost);
	}

}
