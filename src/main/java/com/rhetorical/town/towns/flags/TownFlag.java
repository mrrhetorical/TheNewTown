package com.rhetorical.town.towns.flags;

public enum TownFlag {
	NO_PVP, // Disables pvp within the town border. (default: true)
	ALLOW_MODIFICATION, // Allows residents to break and destroy blocks in plots they are not leasing. (default: true)
	ALLOW_PICKUP, // Allows picking up items by non residents within the town borders. (default: true)
	ALLOW_DROP, // Allows dropping of items by non residents within the town borders. (default: true)
	MOB_SPAWNING, // Disables mob spawning within the town (default: false)
	FIRE_TICK, // Disables fire spread (default: false)
	LAVA_FLOW, // Disables lava flow in the chunk (default: true)
	WATER_FLOW, // Disables water flow in the chunk (default: false)
	ANIMAL_ABUSE, // Disables animal abuse against friendly animals (default: false)
}
