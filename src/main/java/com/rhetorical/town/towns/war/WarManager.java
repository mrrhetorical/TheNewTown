package com.rhetorical.town.towns.war;

import com.rhetorical.town.TheNewTown;
import com.rhetorical.town.files.WarFile;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

public class WarManager {
	private static WarManager instance;

	private Set<War> currentWars = new HashSet<>();

	private WarManager() {

		WarFile file = WarFile.open();

		ConfigurationSection warIds = file.getData().getConfigurationSection("Wars");
		if (warIds != null)
			for (String id : warIds.getKeys(false)) {
				long warId;
				try {
					warId = Long.parseLong(id);
				} catch (Exception e) {
					Bukkit.getLogger().warning(String.format("Could not load war with id '%s'!", id));
					continue;
				}
				War war = new War(warId);
				getCurrentWars().add(war);
			}

	}

	public static WarManager getInstance() {
		if (instance == null)
			instance = new WarManager();

		return instance;
	}

	public Set<War> getCurrentWars() {
		return currentWars;
	}

	public boolean isAtWar(String town) {
		for (War war : currentWars) {
			if (war.getAttacker().equalsIgnoreCase(town) || war.getDefender().equalsIgnoreCase(town))
				return true;
		}

		return false;
	}
	public boolean isAtWar(String a, String b) {
		throw new NotImplementedException();
	}
}
