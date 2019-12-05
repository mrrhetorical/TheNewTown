package com.rhetorical.town.util;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

public class WorldGuardUtil {

	private static WorldGuardUtil instance;

	private final boolean worldGuard;

	private WorldGuardUtil() {
		worldGuard = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null;
	}

	public static WorldGuardUtil getInstance() {
		if (instance == null)
			instance = new WorldGuardUtil();

		return instance;
	}

	public boolean hasWorldGuard() {
		return worldGuard;
	}

	public boolean overlapsRegion(Chunk chunk) {
		if (!hasWorldGuard())
			return false;

		Block blockMin = chunk.getBlock(0, 0, 0), blockMax = chunk.getBlock(15, 255, 15);
		com.sk89q.worldedit.math.BlockVector3 min = com.sk89q.worldedit.math.BlockVector3.at(blockMin.getX(), blockMin.getY(), blockMin.getZ());
		com.sk89q.worldedit.math.BlockVector3 max = com.sk89q.worldedit.math.BlockVector3.at(blockMax.getX(), blockMax.getY(), blockMax.getZ());

		com.sk89q.worldguard.protection.regions.ProtectedRegion region = new com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion(System.currentTimeMillis() + "", min, max);

		com.sk89q.worldguard.protection.managers.RegionManager manager = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer().get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(chunk.getWorld()));

		if (manager == null)
			return false;

		com.sk89q.worldguard.protection.ApplicableRegionSet set = manager.getApplicableRegions(region);

		if (set.getRegions().isEmpty())
			return false;

		return true;
	}
}
