package com.rhetorical.town.towns;

import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.Map;

public class TownManager {
	private static TownManager instance;

	private Map<String, Town> towns = new HashMap<>();

	private TownManager() {

	}

	public static TownManager getInstance() {
		if (instance == null)
			instance = new TownManager();

		return instance;
	}

	public Map<String, Town> getTowns() {
		return towns;
	}

	public Town getTown(String name) {
		if (towns.containsKey(name))
			return towns.get(name);
		return null;
	}

	public boolean isChunkClaimed(Chunk chunk) {
		for (Town town : getTowns().values())
			if (town.isChunkClaimed(chunk))
				return true;
		return false;
	}

	TownType getPreviousTownType(TownType current) {
		switch(current) {
			case CITY_STATE:
				return TownType.MAJOR_CITY;
			case MAJOR_CITY:
				return TownType.CITY;
			case CITY:
				return TownType.TOWN;
			case TOWN:
				return TownType.VILLAGE;
			case VILLAGE:
				return TownType.HAMLET;
			default:
				return TownType.HAMLET;
		}
	}

	TownType getNextTownType(TownType current) {
		switch(current) {
			case HAMLET:
				return TownType.VILLAGE;
			case VILLAGE:
				return TownType.TOWN;
			case TOWN:
				return TownType.CITY;
			case CITY:
				return TownType.MAJOR_CITY;
			case MAJOR_CITY:
				return TownType.CITY_STATE;
			default:
				return TownType.CITY_STATE;
		}
	}

	public int getTaxablePlots(Town town) {
		int base = town.getTownType() == TownType.HAMLET ? 0 : getPreviousTownType(town.getTownType()).getMaxPlots();
		return town.getPlots().size() - base;
	}

	//todo: implement
	public float getUpkeep(Town town) {



//		return town.getTownType().getFlatCost() *
		return -1f;
	}
}
