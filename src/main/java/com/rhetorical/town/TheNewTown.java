package com.rhetorical.town;

import com.rhetorical.town.commands.CommandCompleter;
import com.rhetorical.town.commands.TownCommand;
import com.rhetorical.town.towns.TownManager;
import com.rhetorical.town.util.EnterMessageLocation;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class TheNewTown extends JavaPlugin {

	private static TheNewTown instance;

	private boolean worldGuard;

	private Economy economy;

	private float creationCost = 1000f;

	private final long landMultiplier = 10L;
	private final long killMultiplier = 1L;

	private EnterMessageLocation enterMessageLocation;

	@Override
	public void onEnable() {
		if (instance != null)
			return;

		instance = this;

		worldGuard = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null;

		saveDefaultConfig();
		reloadConfig();

		setCreationCost((float) getConfig().getDouble("creation_cost"));

		String enterLocation = getConfig().getString("enterMessageLocation");
		try {
			setEnterMessageLocation(EnterMessageLocation.valueOf(enterLocation));
		} catch (Exception e) {
			Bukkit.getLogger().severe("Invalid enter message location: " + enterLocation);
			setEnterMessageLocation(EnterMessageLocation.CHAT);
		}

		setupEconomy();

		TownManager.getInstance(); // Load towns

		getCommand("t").setTabCompleter(new CommandCompleter());
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

	public boolean hasWorldGuard() {
		return worldGuard;
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

	public long getLandMultiplier() {
		return landMultiplier;
	}

	public long getKillMultiplier() {
		return killMultiplier;
	}

	public EnterMessageLocation getEnterMessageLocation() {
		return enterMessageLocation;
	}

	public void setEnterMessageLocation(EnterMessageLocation enterMessageLocation) {
		this.enterMessageLocation = enterMessageLocation;
	}
}
