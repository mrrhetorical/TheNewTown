package com.rhetorical.town.towns.war;

import com.rhetorical.town.files.WarFile;
import com.rhetorical.town.util.ChunkInfo;
import com.rhetorical.town.util.DateTimeConverter;
import org.bukkit.Bukkit;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class War {

	private final long id;

	private LocalDateTime startTime;
	private LocalDateTime endTime;

	private WarGoalObjective warGoal;

	private String attacker;
	private String defender;

	private WarInventory attackingInventory;
	private WarInventory defendingInventory;


	private long attackingStartingWarPower, defendingStartingWarPower;

	private long attackingKills, defendingKills;

	private Set<ChunkInfo> attackerOccupiedPlots = new HashSet<>(),
			defenderOccupiedPlots = new HashSet<>();

	private Set<String> attackingAllies = new HashSet<>();
	private Set<String> defendingAllies = new HashSet<>();


	public War(long id) {
		this.id = id;

		WarFile file = WarFile.open();

		String base = "Wars." + id;

		String warGoalString = file.getData().getString(base + ".warGoal");

		try {
			if (warGoalString == null)
				throw new Exception();
			warGoal = WarGoalObjective.valueOf(warGoalString.toUpperCase());
		} catch (Exception e) {
			Bukkit.getLogger().severe(String.format("Could not load war goal for war with id '%s'!", getId()));
			return;
		}

		attacker = file.getData().getString(base + ".attacker");
		defender = file.getData().getString(base + ".defender");

		startTime = DateTimeConverter.convert(file.getData().getString(base + ".startTime"));
		endTime = DateTimeConverter.convert(file.getData().getString(base + ".endTime"));

		attackingStartingWarPower = file.getData().getLong(base + ".attackingStartingWarPower");
		defendingStartingWarPower = file.getData().getLong(base + ".defendingStartingWarPower");

		attackingKills = file.getData().getLong(base + ".attackingKills");
		defendingKills = file.getData().getLong(base + ".defendingKills");

		List<String> sAOP = file.getData().getStringList(base + ".attackerOccupiedPlots"),
				sDOP = file.getData().getStringList(base + ".defenderOccupiedPlots");

		for (String s : sAOP) {
			ChunkInfo chunkInfo = ChunkInfo.fromString(s);
			if (chunkInfo != null)
				getAttackerOccupiedPlots().add(chunkInfo);
		}

		for (String s : sDOP) {
			ChunkInfo chunkInfo = ChunkInfo.fromString(s);
			if (chunkInfo != null)
				getDefenderOccupiedPlots().add(chunkInfo);
		}

		attackingAllies =  new HashSet<>(file.getData().getStringList(base + ".attackingAllies"));
		defendingAllies = new HashSet<>(file.getData().getStringList(base + ".defendingAllies"));

		//todo: set up inventories
	}

	private War(String a, String d, WarFile file) {
		long k = 0;
		while (file.getData().contains("wars." + k))
			k++;
		id = k;

		attacker = a;
		defender = d;
	}

	public static War createWar(String attacker, String defender, WarFile file) {
		return new War(attacker, defender, file);
	}

	public void save() {

	}

	public String getAttacker() {
		return attacker;
	}

	public String getDefender() {
		return defender;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public long getId() {
		return id;
	}

	public WarGoalObjective getWarGoal() {
		return warGoal;
	}

	public WarInventory getAttackingInventory() {
		return attackingInventory;
	}

	public WarInventory getDefendingInventory() {
		return defendingInventory;
	}

	public long getAttackingStartingWarPower() {
		return attackingStartingWarPower;
	}

	public long getDefendingStartingWarPower() {
		return defendingStartingWarPower;
	}

	public long getAttackingKills() {
		return attackingKills;
	}

	public long getDefendingKills() {
		return defendingKills;
	}

	public Set<ChunkInfo> getAttackerOccupiedPlots() {
		return attackerOccupiedPlots;
	}

	public Set<ChunkInfo> getDefenderOccupiedPlots() {
		return defenderOccupiedPlots;
	}

	public Set<String> getAttackingAllies() {
		return attackingAllies;
	}

	public Set<String> getDefendingAllies() {
		return defendingAllies;
	}
}
