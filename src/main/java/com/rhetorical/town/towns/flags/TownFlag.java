package com.rhetorical.town.towns.flags;

public enum TownFlag {
	NO_PVP(true), // Disables pvp within the town border. (default: true)
	ALLOW_MODIFICATION(true), // Allows residents to break and destroy blocks in plots they are not leasing. (default: true)
	ALLOW_PICKUP(true), // Allows picking up items by non residents within the town borders. (default: true)
	ALLOW_DROP(true), // Allows dropping of items by non residents within the town borders. (default: true)
	MOB_SPAWNING(false), // Disables mob spawning within the town (default: false)
	FIRE_TICK(false), // Disables fire spread (default: false)
	LAVA_FLOW(false), // Disables lava flow in the chunk (default: false)
	WATER_FLOW(false), // Disables water flow in the chunk (default: false)
	ANIMAL_ABUSE(false), // Disables animal abuse against friendly animals (default: false)
	ALIEN_INTERACT(false), // Should non-residents be able to interact with items within the town? (default: false)
	;

	final boolean defaultValue;

	TownFlag(boolean value) {
		defaultValue = value;
	}

	public boolean getDefaultValue() {
		return defaultValue;
	}
}
