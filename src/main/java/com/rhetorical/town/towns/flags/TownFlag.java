package com.rhetorical.town.towns.flags;

import java.util.ArrayList;
import java.util.List;

public enum TownFlag {
	NO_PVP(true, "PvP", "True: PvP is enabled in town."), // Disables pvp within the town border. (default: true) // done
	ALLOW_MODIFICATION(true, "Block Griefing", "True: Residents may break blocks in any plot."), // Allows residents to break and destroy blocks in plots they are not leasing. (default: true) // done
	ALLOW_PICKUP(true, "Item Pickup", "True: Non-residents may pickup items in town."), // Allows picking up items by non residents within the town borders. (default: true) // done
	ALLOW_DROP(true, "Item Drop", "True: Non-residents may drop items in town."), // Allows dropping of items by non residents within the town borders. (default: true) // done
	MOB_SPAWNING(false, "Monster Spawning", "True: Monsters spawn in town."), // Disables mob spawning within the town (default: false) // done
	FIRE_TICK(false, "Fire Spread", "True: Fire spreads in town."), // Disables fire spread (default: false) // done
	LAVA_FLOW(false, "Lava Flow", "True: Lava flows in town."), // Disables lava flow in the chunk (default: false) // done
	WATER_FLOW(false, "Water Flow", "True: Water flows in the town."), // Disables water flow in the chunk (default: false) // done
	ANIMAL_ABUSE(false, "Kill Passive Animals", "True: Players cannot kill passive mobs."), // Disables animal abuse against friendly animals (default: false) // done
	ALIEN_INTERACT(false, "Interaction", "True: Allows non-residents to open doors, pull levers, loot chests, etc."), // Should non-residents be able to interact with items within the town? (default: false) // done
	;

	private final boolean defaultValue;
	private String name;
	private String description;

	TownFlag(boolean defaultValue, String name, String description) {
		this.defaultValue = defaultValue;
		this.name = name;
		this.description = description;
	}

	public boolean getDefaultValue() {
		return defaultValue;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public static List<String> stringValues() {
		List<String> v = new ArrayList<>();
		for (TownFlag flag : TownFlag.values())
			v.add(flag.toString().toLowerCase());

		return v;
	}
}
