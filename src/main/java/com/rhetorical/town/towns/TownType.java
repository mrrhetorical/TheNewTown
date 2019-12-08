package com.rhetorical.town.towns;

import com.rhetorical.town.TheNewTown;

public enum TownType {
	HAMLET("Hamlet", 8, 50f, 2f), VILLAGE("Village", 16, 70f, 2.5f), TOWN("Town", 32, 100f, 4f), CITY("City", 64, 180f, 5f), MAJOR_CITY("Major City", 128, 400f, 7.5f), CITY_STATE("City State", -1, 700f, 10f), NULL("NULL", -1, -1f, -1f);

	private final String readableName;
	private int maxPlots;
	private float flatCost;
	private float plotCost;
	private float claimCost;

	TownType(String rn, int plots, float flat, float rate) {
		readableName = rn;
		maxPlots = plots;
		flatCost = flat;
		plotCost = rate;

		if (maxPlots == -1 && flatCost == -1f && plotCost == -1f)
			return;

		maxPlots = TheNewTown.getInstance().getConfig().getInt(String.format("town_tiers.%s.max_plots", toString().toLowerCase()));
		flatCost = (float) TheNewTown.getInstance().getConfig().getDouble(String.format("town_tiers.%s.upkeep.flat_rate", toString().toLowerCase()));
		plotCost = (float) TheNewTown.getInstance().getConfig().getDouble(String.format("town_tiers.%s.upkeep.plot_cost", toString().toLowerCase()));
		claimCost = (float) TheNewTown.getInstance().getConfig().getDouble(String.format("town_tiers.%s.claim_fee", toString().toLowerCase()));
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

	public float getClaimCost() {
		return claimCost;
	}

	public static TownType getFromId(int id) {
		if (id >= TownType.values().length - 1)
			return NULL;
		return TownType.values()[id];
	}

	public String getReadable() {
		return readableName;
	}

}
