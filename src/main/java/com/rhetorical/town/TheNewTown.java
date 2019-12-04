package com.rhetorical.town;

import com.rhetorical.town.commands.TownCommand;
import com.rhetorical.town.towns.TownManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class TheNewTown extends JavaPlugin {

	private static TheNewTown instance;

	private Economy economy;

	private float creationCost = 1000f;

	@Override
	public void onEnable() {
		if (instance != null)
			return;

		instance = this;

		saveDefaultConfig();
		reloadConfig();

		setCreationCost((float) getConfig().getDouble("creation_cost"));

		setupEconomy();

		TownManager.getInstance(); // Load towns
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

	private void setCreationCost(float value) {
		creationCost = value;
	}

	public float getCreationCost() {
		return creationCost;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!label.equalsIgnoreCase("town") && !label.equalsIgnoreCase("t"))
			return false;

		TownCommand.getInstance().onCommand(sender, cmd, label, args);

		return true;
	}

}
