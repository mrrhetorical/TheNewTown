package com.rhetorical.town.towns;

import com.rhetorical.town.TheNewTown;

public enum TownType {
	HAMLET(8, 50f, 2f), VILLAGE(16, 70f, 2.5f), TOWN(32, 100f, 4f), CITY(64, 180f, 5f), MAJOR_CITY(128, 400f, 7.5f), CITY_STATE(-1, 700f, 10f), NULL(-1, -1f, -1f);

	private int maxPlots;
	private float flatCost;
	private float plotCost;

	TownType(int plots, float flat, float rate) {
		maxPlots = plots;
		flatCost = flat;
		plotCost = rate;

		if (maxPlots == -1 && flatCost == -1f && plotCost == -1f)
			return;

		maxPlots = TheNewTown.getInstance().getConfig().getInt(String.format("town_tiers.%s.max_plots", toString().toLowerCase()));
		flatCost = (float) TheNewTown.getInstance().getConfig().getDouble(String.format("town_tiers.%s.upkeep.flat_rate", toString().toLowerCase()));
		plotCost = (float) TheNewTown.getInstance().getConfig().getDouble(String.format("town_tiers.%s.upkeep.plot_cost", toString().toLowerCase()));
	}

	public int getMaxPlots() {
		return maxPlots;
	}

	public float getFlatCost() {
		return flatCost;
	}

	public float getPlotCost() {
		return plotCost;
	}

	public static TownType getFromId(int id) {
		if (id >= TownType.values().length - 1)
			return NULL;
		return TownType.values()[id];
	}

}
