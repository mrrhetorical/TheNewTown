package com.rhetorical.town.towns.war;

import org.apache.commons.lang.NotImplementedException;

public class WarManager {
	private static WarManager instance;

	private WarManager() {

		//todo: load wars from config

	}

	public static WarManager getInstance() {
		if (instance == null)
			instance = new WarManager();

		return instance;
	}

	public boolean isAtWar(String a, String b) {
		throw new NotImplementedException();
	}
}
