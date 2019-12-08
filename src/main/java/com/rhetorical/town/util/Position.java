package com.rhetorical.town.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Position {

	private float x, y, z;
	private String world;

	public Position(float x, float y, float z, String world) {
		setX(x);
		setY(y);
		setZ(z);
		setWorld(world);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	@Override
	public String toString() {
		return getWorld() + "::" + getX() + "::" + getY() + "::" + getZ();
	}

	public static Position fromString(String s) {
		float x, y, z;
		String world;
		String args[] = s.split("::");
		if (args.length != 4)
			return null;

		world = args[0];

		try {
			x = Float.parseFloat(args[1]);
			y = Float.parseFloat(args[2]);
			z = Float.parseFloat(args[3]);
		} catch (Exception ignored) {
			return null;
		}

		return new Position(x, y, z, world);
	}

	public static Position fromLocation(Location loc) {
		return new Position((float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), loc.getWorld().getName());
	}

	public Location toLocation() {
		return new Location(Bukkit.getWorld(getWorld()), getX(), getY(), getZ());
	}
}
