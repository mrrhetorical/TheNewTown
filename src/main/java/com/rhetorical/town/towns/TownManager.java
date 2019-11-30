package com.rhetorical.town.towns;

import java.util.HashSet;
import java.util.Set;

public class TownManager {
	private static TownManager instance;

	private Set<Town> towns = new HashSet<>();

	private TownManager() {

	}

	public static TownManager getInstance() {
		if (instance == null)
			instance = new TownManager();

		return instance;
	}

	public Set<Town> getTowns() {
		return towns;
	}

	public Town getTown(String name) {
		for (Town t : getTowns())
			if (t.getName().equalsIgnoreCase(name))
				return t;
		return null;
	}
}
