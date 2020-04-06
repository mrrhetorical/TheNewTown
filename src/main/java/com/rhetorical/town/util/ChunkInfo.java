package com.rhetorical.town.util;

public class ChunkInfo {

	private int x, y;
	private String world;

	public ChunkInfo(int x, int y, String world) {
		this.x = x;
		this.y = y;
		this.world = world;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getWorld() {
		return world;
	}

	public ChunkInfo add(int x, int y) {
		return new ChunkInfo(getX() + x, getY() + y, world);
	}

	public String toString() {
		return x + "::" + y + "::" + world;
	}

	public static ChunkInfo fromString(String s) {
		String[] arr = s.split("::");
		try {
			return new ChunkInfo(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), arr[2]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
