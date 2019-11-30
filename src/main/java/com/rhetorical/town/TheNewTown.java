package com.rhetorical.town;

import org.bukkit.plugin.java.JavaPlugin;

public class TheNewTown extends JavaPlugin {

	private static TheNewTown instance;

	@Override
	public void onEnable() {
		if (instance != null)
			return;

		instance = this;
	}

	public static TheNewTown getInstance() {
		return instance;
	}
}
