package com.rhetorical.town.util;

import com.rhetorical.town.TheNewTown;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

public class WorldGuardUtil {

	private static WorldGuardUtil instance;

	private WorldGuardUtil() {}

	public static WorldGuardUtil getInstance() {
		if (instance == null)
			instance = new WorldGuardUtil();

		return instance;
	}

	public boolean overlapsRegion(Chunk chunk) {
		if (!TheNewTown.getInstance().hasWorldGuard())
			return false;

		try {

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
		} catch (Error|Exception ignored) {
			return false;
		}
	}
}
