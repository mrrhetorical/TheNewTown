package com.rhetorical.town;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class TheNewTown extends JavaPlugin {

	private static TheNewTown instance;

	private Economy economy;

	@Override
	public void onEnable() {
		if (instance != null)
			return;

		instance = this;

		setupEconomy();
	}

	public static TheNewTown getInstance() {
		return instance;
	}

	public Economy getEconomy() {
		return economy;
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}
}
