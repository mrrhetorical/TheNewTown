package com.rhetorical.town.towns.flags;

import java.util.ArrayList;
import java.util.List;

public enum TownFlag {
	NO_PVP(true, "Disallow PvP", "Disallow's pvp within the town's borders."), // Disables pvp within the town border. (default: true) // done
	ALLOW_MODIFICATION(true, "Allow block griefing", "Allows residents to break and destroy blocks in plots within the town they are not leasing."), // Allows residents to break and destroy blocks in plots they are not leasing. (default: true) // done
	ALLOW_PICKUP(true, "Allow item pickup", "Allows non residents to pick up items within the town border."), // Allows picking up items by non residents within the town borders. (default: true) // done
	ALLOW_DROP(true, "Allow item drop", "Allows non residents to drop items within the town border."), // Allows dropping of items by non residents within the town borders. (default: true) // done
	MOB_SPAWNING(false, "Allow monster spawning", "Disables mob spawning within the town."), // Disables mob spawning within the town (default: false) // done
	FIRE_TICK(false, "Allow fire spread", "Disables fire spread within the town."), // Disables fire spread (default: false) // done
	LAVA_FLOW(false, "Allow lava flow", "Disables lava flow within the town."), // Disables lava flow in the chunk (default: false) // done
	WATER_FLOW(false, "Allow water flow", "Disables water flow within the town."), // Disables water flow in the chunk (default: false) // done
	ANIMAL_ABUSE(false, "Disallow animal abuse", "Prevents players from killing non-hostile mobs."), // Disables animal abuse against friendly animals (default: false) // done
	ALIEN_INTERACT(false, "Allow nonresident interaction", "Allows non residents to interact with items within the town."), // Should non-residents be able to interact with items within the town? (default: false) // done
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
