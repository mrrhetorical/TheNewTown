package com.rhetorical.town.towns.war;

import java.util.HashSet;
import java.util.Set;

public class War {

	private long id;

	private String attacker;
	private String defender;



	private long attackingStartingWarPower, defendingStartingWarPower;

	private long attackingKills, defendingKills;

	private Set<String> attackerOccupiedPlots = new HashSet<>(),
			defenderOccupiedPlots = new HashSet<>();

	private Set<String> attackingAllies = new HashSet<>();
	private Set<String> defendingAllies = new HashSet<>();
}
