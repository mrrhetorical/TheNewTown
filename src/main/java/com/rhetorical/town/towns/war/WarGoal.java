package com.rhetorical.town.towns.war;

import com.rhetorical.town.files.TownFile;
import com.rhetorical.town.towns.Town;
import com.rhetorical.town.towns.TownManager;
import com.rhetorical.town.util.DateTimeConverter;
import org.bukkit.Bukkit;

import java.time.LocalDateTime;

public class WarGoal {

	private final long id;
	private WarGoalObjective objective;
	private String target;
	private String attacker;
	private LocalDateTime startDate;
	private LocalDateTime completionDate;
	private LocalDateTime expiryDate;

	/**
	 * Constructor used for creating a war goal
	 * */
	public WarGoal(String attacker, String target, WarGoalObjective objective, TownFile file, String base) {
		int k = 0;
		while(file.getData().contains(base + "." + k))
			k++;

		this.id = k;

		setAttacker(attacker);
		setTarget(target);
		setStartDate(LocalDateTime.now());
		setObjective(objective);
		generateCompletionAndExpiryTime();
	}

	/**
	 * Constructor used for loading a war goal
	 * */
	public WarGoal(long id, String attacker, TownFile file) {
		this.id = id;

		setAttacker(attacker);

		String objStr = file.getData().getString(getAttacker() + ".warGoals." + getId() + ".objective");
		WarGoalObjective obj = null;
		try {
			obj = WarGoalObjective.valueOf(objStr);
		} catch (Exception e) {
			Bukkit.getLogger().severe(String.format("Bad objective data in war goal with id %s in town %s", getId(), getAttacker()));
			e.printStackTrace();
		}

		setObjective(obj);

		setTarget(file.getData().getString(getAttacker() + ".warGoals." + getId() + ".target"));

		setStartDate(DateTimeConverter.convert(file.getData().getString(getAttacker() + ".warGoals." + getId() + ".startDate")));
		setCompletionDate(DateTimeConverter.convert(file.getData().getString(getAttacker() + ".warGoals." + getId() + ".completionDate")));
		setExpiryDate(DateTimeConverter.convert(file.getData().getString(getAttacker() + ".warGoals." + getId() + ".expiryDate")));
	}

	private void generateCompletionAndExpiryTime() {
		Town attacking = TownManager.getInstance().getTown(getAttacker()),
				defending = TownManager.getInstance().getTown(getTarget());

		if (attacking == null || defending == null) {
			Bukkit.getLogger().severe("GENERATED WAR GOAL WHERE AT LEAST ONE TOWN DID NOT EXIST!");
			return;
		}

		long a = attacking.getWarPower(),
				d = defending.getWarPower();

		long modifier = d - a;

		// 24 hours * 60 minutes = 1440 minutes per day

		long minutes = 1440 + modifier;
		minutes = minutes < 20 ? 20 : minutes;

		setCompletionDate(getStartDate().plusMinutes(minutes));

		//todo - make this configurable
		//expiry date 48 hours after completion date
		setExpiryDate(getCompletionDate().plusHours(48));
	}

	public boolean isCompleted() {
		return getCompletionDate().isBefore(LocalDateTime.now());
	}

	public boolean isExpired() {
		return getExpiryDate().isBefore(LocalDateTime.now());
	}

	public WarGoalObjective getObjective() {
		return objective;
	}

	void setObjective(WarGoalObjective objective) {
		this.objective = objective;
	}

	public String getTarget() {
		return target;
	}

	void setTarget(String target) {
		this.target = target;
	}

	public String getAttacker() {
		return attacker;
	}

	void setAttacker(String attacker) {
		this.attacker = attacker;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getCompletionDate() {
		return completionDate;
	}

	void setCompletionDate(LocalDateTime completionDate) {
		this.completionDate = completionDate;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public void save(TownFile file, String base) {
		file.getData().set(base + ".objective", getObjective().toString());
		file.getData().set(base + ".target", getTarget());
		file.getData().set(base + ".startDate", DateTimeConverter.convert(getStartDate()));
		file.getData().set(base + ".completionDate", DateTimeConverter.convert(getCompletionDate()));
		file.getData().set(base + ".expiryDate", DateTimeConverter.convert(getExpiryDate()));
	}

	public long getId() {
		return id;
	}

}
