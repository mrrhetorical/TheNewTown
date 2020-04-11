package com.rhetorical.town.towns;

import com.rhetorical.town.TheNewTown;
import com.rhetorical.town.files.TownFile;
import com.rhetorical.town.towns.flags.TownFlag;
import com.rhetorical.town.towns.war.WarGoal;
import com.rhetorical.town.towns.war.WarGoalObjective;
import com.rhetorical.town.towns.war.WarInventory;
import com.rhetorical.town.towns.war.WarManager;
import com.rhetorical.town.util.DateTimeConverter;
import com.rhetorical.town.util.Position;
import com.rhetorical.town.util.WorldGuardUtil;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Town {

	private UUID mayor;
	private String name;

	private List<Plot> plots = new ArrayList<>();

	private List<UUID> residents = new ArrayList<>();

	private List<WarGoal> activeWarGoals = new ArrayList<>();

	private TownType townType;

	private float tax = 0f;

	private double bank = 0f;

	private LocalDateTime lastTaxPeriod;
	private LocalDateTime lastUpkeepPeriod;

	private Map<TownFlag, Boolean> flags = new HashMap<>();

	private Position home;

	private TownInventory inventory;
	private WarInventory warInventory;

	private Set<String> allies = new HashSet<>();

	/**
	 * Used in town loading.
	 * */
	public Town(String name) {
		TownFile file = TownFile.open();

		if (!file.getData().contains(name))
			return;

		setName(name);

		String m = file.getData().getString(name + ".mayor");
		if (m == null)
			return;
		setMayor(UUID.fromString(m));

		List<String> residents = file.getData().getStringList(name + ".residents");
		for (String key : residents) {
			UUID player = UUID.fromString(key);
			getResidents().add(player);
		}

		setTax((float) file.getData().getDouble(name + ".tax"));


		lastTaxPeriod = DateTimeConverter.convert(file.getData().getString(getName() + ".lastTaxPeriod"));
		lastUpkeepPeriod = DateTimeConverter.convert(file.getData().getString(getName() + ".lastUpkeepPeriod"));

		String pos = file.getData().getString(getName() + ".home");
		if (pos != null) {
			Position h = Position.fromString(pos);
			if (h != null)
				setHome(h);
		}

		ConfigurationSection f = file.getData().getConfigurationSection(getName() + ".flags");
		if (f != null)
			for (String key : f.getKeys(false)) {
				TownFlag flag;
				try {
					flag = TownFlag.valueOf(key.toUpperCase());
				} catch (Exception ignored) { continue; }

				boolean v = file.getData().getBoolean(getFlags()  + ".flags." + key);
				setFlag(flag, v);
			}


		ConfigurationSection w = file.getData().getConfigurationSection(getName() + ".warGoals");
		if (f != null)
			for (String key : f.getKeys(false)) {
				long id;
				try {
					id = Long.parseLong(key);
				} catch (Exception ignored) { continue; }

				WarGoal goal = new WarGoal(id, getName(), file);
				getActiveWarGoals().add(goal);
			}


		for (TownFlag flag : TownFlag.values())
			if (!getFlags().containsKey(flag))
				getFlags().put(flag, flag.getDefaultValue());

		allies = new HashSet<>(file.getData().getStringList(getName() + ".allies"));

		bank = file.getData().getDouble(getName() + ".balance");

		inventory = new TownInventory(getName());
		warInventory = new WarInventory(getName());
	}

	/**
	 * Used in town creation & initialization.
	 * */
	public Town(UUID mayor, Chunk initial, String name) throws PlotAlreadyExistsException {
		Bukkit.getServer().getLogger().info("Created town");
		setMayor(mayor);
		setTax(0f);
		residents.add(mayor);
		setName(name);
		setHome(null);
		setTownType(TownType.HAMLET);
		for (TownFlag flag : TownFlag.values()) {
			setFlag(flag, flag.getDefaultValue());
		}
		if (!addPlot(initial)) {
			PlotAlreadyExistsException.FailReason reason;
			if (!TownManager.getInstance().isChunkClaimed(initial))
				reason = PlotAlreadyExistsException.FailReason.ALREADY_CLAIMED;
			else
				reason = PlotAlreadyExistsException.FailReason.REGION_PROTECTION;

				throw new PlotAlreadyExistsException(initial, reason);
		}

		inventory = new TownInventory(getName());
	}

	void loadPlots(TownFile file) {
		ConfigurationSection plots = file.getData().getConfigurationSection(name + ".plots");
		for (String key : plots.getKeys(false)) {
			long id;
			try {
				id = Long.parseLong(key);
			} catch (Exception ignored) { continue; }
			Plot plot = Plot.loadPlot(name, id, file);
			if (plot == null)
				continue;
			getPlots().add(plot);
		}

		townType = calculateTownSize(getPlots().size());
	}

	public void save() {

		TownFile file = TownFile.open();

		file.getData().set(getName() + ".residents", null);
		List<String> r = new ArrayList<>();
		for (UUID resident : getResidents())
			r.add(resident.toString());

		file.getData().set(getName() + ".residents", r);

		file.getData().set(getName() + ".mayor", getMayor().toString());

		file.getData().set(getName() + ".tax", getTax());

		file.getData().set(getName() + ".home", getHome() != null ? getHome().toString() : "none");


		file.getData().set(getName() + ".lastTaxPeriod", DateTimeConverter.convert(lastTaxPeriod));

		file.getData().set(getName() + ".lastUpkeepPeriod", DateTimeConverter.convert(lastUpkeepPeriod));

		file.getData().set(getName() + ".allies", new ArrayList<>(getAllies()));

		file.getData().set(getName() + ".plots", null);

		for (TownFlag flag : getFlags().keySet())
			file.getData().set(getName() + ".flags." + flag.toString().toLowerCase(), getFlags().get(flag));

		for (Plot plot : getPlots()) {
			plot.save(getName(), file);
		}

		for (WarGoal goal : getActiveWarGoals()) {
			String base = getName() + ".warGoals." + goal.getId();
			goal.save(file, base);
		}

		file.saveData();
	}

	public UUID getMayor() {
		return mayor;
	}

	public void setMayor(UUID value) {
		mayor = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String value) {
		name = value;
	}

	public List<Plot> getPlots() {
		return plots;
	}

	public List<UUID> getResidents() {
		return residents;
	}

	public float getTax() {
		return tax;
	}

	public void setTax(float value) {
		tax = value;
	}

	public void setHome(Position position) {
		home = position;
	}

	public Position getHome() {
		return home;
	}

	public TownInventory getInventory() {
		return inventory;
	}

	public WarInventory getWarInventory() {
		return warInventory;
	}

	public Set<String> getAllies() {
		return allies;
	}

	public double getBank() {
		return bank;
	}

	public void setBank(double value) {
		bank = value;
	}

	public List<WarGoal> getActiveWarGoals() {
		return activeWarGoals;
	}

	public boolean addPlot(Chunk chunk) {
		if (TownManager.getInstance().isChunkClaimed(chunk))
			return false;

		if(TheNewTown.getInstance().hasWorldGuard() && WorldGuardUtil.getInstance().overlapsRegion(chunk))
			return false;

		Plot plot = new Plot(generatePlotId(), getMayor(), null, chunk, false, 0f, getName(), new HashMap<>());
		getPlots().add(plot);

		if (getTownType() == null)
			setTownType(TownType.HAMLET);
		else if (getPlots().size() > getTownType().getMaxPlots())
			setTownType(TownManager.getInstance().getNextTownType(getTownType()));

		return true;
	}

	public boolean removePlot(Chunk chunk) {
		if (!isChunkClaimed(chunk))
			return false;

		for (Plot plot : new ArrayList<>(getPlots()))
			if (plot.isChunk(chunk)) {
				getPlots().remove(plot);
				plot.unregister();
			}

		if (getPlots().size() <= TownManager.getInstance().getPreviousTownType(getTownType()).getMaxPlots()) {
			setTownType(TownManager.getInstance().getPreviousTownType(getTownType()));
		}

		if (getPlots().size() == 0)
			TownManager.getInstance().deleteTown(getName());
		else
			save();

		return true;
	}

	public Plot getPlot(Chunk chunk) {
		for (Plot plot : getPlots()) {
			if (plot.getX() == chunk.getX() && plot.getZ() == chunk.getZ() && plot.getWorldName().equalsIgnoreCase(chunk.getWorld().getName()))
				return plot;
		}

		return null;
	}

	public Plot getPlot(String worldName, int x, int z) {
		for (Plot plot : getPlots()) {
			if (plot.getX() == x && plot.getZ() == z && plot.getWorldName().equalsIgnoreCase(worldName))
				return plot;
		}

		return null;
	}

	public boolean addPlayer(UUID target) {
		if (getResidents().contains(target))
			return false;

		getResidents().add(target);
		return true;
	}

	public boolean removePlayer(UUID target) {
		if (!getResidents().contains(target))
			return false;

		if (target.equals(getMayor()))
			return false;

		getResidents().remove(target);
		return true;
	}

	void delete() {
		for (Plot plot : getPlots()) {
			plot.unregister();
		}
		inventory.unregister();
		warInventory.unregister();
	}

	public TownType getTownType() {
		return townType;
	}

	public void setTownType(TownType value) {
		townType = value;
	}

	public boolean isChunkClaimed(Chunk chunk) {
		for (Plot plot : getPlots())
			if (plot.isChunk(chunk))
				return true;

		return false;
	}

	private long generatePlotId() {
		long id = 0;
		List<Long> used = new ArrayList<>();
		for (Plot plot : getPlots())
			used.add(plot.getId());

		while(used.contains(id))
			id++;

		return id;
	}

	private void demote() {
		if (getTownType() == TownType.HAMLET) {
			TownManager.getInstance().deleteTown(getName());
			return;
		}

		setTownType(TownManager.getInstance().getPreviousTownType(getTownType()));

		while (getPlots().size() > getTownType().getMaxPlots()) {
			getPlots().remove(getPlots().size() - 1); //remove most recently added plot
		}
	}

	void collectTaxes() {

		LocalDateTime now = LocalDateTime.now();

		if (lastTaxPeriod == null)
			lastTaxPeriod = now;
		if (lastUpkeepPeriod == null)
			lastUpkeepPeriod = now;

		if (ChronoUnit.HOURS.between(now, lastTaxPeriod) >= TownManager.getInstance().getTaxPeriod()) {
			lastTaxPeriod = now;
			List<UUID> residents = new ArrayList<>(getResidents());
			double collected = 0d;
			for (UUID resident : residents) {
				if (getMayor().equals(resident))
					continue;
				EconomyResponse r = TheNewTown.getInstance().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(resident), (double) getTax());
				if (!r.transactionSuccess())
					getResidents().remove(resident);
				else
					collected += (double) getTax();
			}

			for (Plot plot : getPlots()) {
				collected += plot.collectRent();
			}

			setBank(getBank() + collected);

//			EconomyResponse r = TheNewTown.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(getMayor()), collected);
//			if (!r.transactionSuccess())
//				Bukkit.getLogger().severe("Could not deposit money into mayor of " + getName() + "'s account!");
		}

		if (ChronoUnit.HOURS.between(now, lastUpkeepPeriod) >= TownManager.getInstance().getUpkeepPeriod()) {
			lastUpkeepPeriod = now;

//			EconomyResponse tax = TheNewTown.getInstance().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(getMayor()), (double) TownManager.getInstance().getUpkeep(this));
			double resultantBalance = getBank() - TownManager.getInstance().getUpkeep(this);
			if (resultantBalance < 0f)
				demote();
			else
				setBank(resultantBalance);

		}

		save();
	}

	private static TownType calculateTownSize(int plots) {

		if (plots > TownType.MAJOR_CITY.getMaxPlots())
			return TownType.CITY_STATE;
		else if (plots > TownType.CITY.getMaxPlots())
			return TownType.MAJOR_CITY;
		else if (plots > TownType.TOWN.getMaxPlots())
			return TownType.CITY;
		else if (plots > TownType.VILLAGE.getMaxPlots())
			return TownType.TOWN;
		else if (plots > TownType.HAMLET.getMaxPlots())
			return TownType.VILLAGE;
		else
			return TownType.HAMLET;
	}

	public Map<TownFlag, Boolean> getFlags() {
		return flags;
	}

	public boolean getFlag(TownFlag flag) {
		if (!getFlags().containsKey(flag))
			getFlags().put(flag, flag.getDefaultValue());

		return getFlags().get(flag);
	}

	public void setFlag(TownFlag flag, boolean value) {
		getFlags().put(flag, value);
	}

	public void showBorder(Player player) {

		int borderDistance = 40000;

		for (Plot plot : getPlots()) {
			Chunk chunk = Bukkit.getWorld(plot.getWorldName()).getChunkAt(plot.getX(), plot.getZ());
			if (player.getLocation().distanceSquared(chunk.getBlock(0, player.getLocation().getBlockY(), 0).getLocation()) < borderDistance)
				plot.playBorderParticle(this, (int) player.getLocation().getY(), player);
		}
	}

	public boolean isAlly(String town) {
		return getAllies().contains(town);
	}

	public boolean addAlly(String town) {
		if (!WarManager.getInstance().isAtWar(getName(), town)) {
			getAllies().add(town);
			return true;
		}

		return false;
	}

	public boolean isTownAdmin(UUID target) {
		return getMayor().equals(target); //todo: include town deputies in future
	}

	public boolean withdrawFromBank(UUID target, float amount) {
		if (getBank() - amount <= 0f)
			return false;

		if (!isTownAdmin(target))
			return false;

		EconomyResponse request = TheNewTown.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(target), amount);
		if (!request.transactionSuccess())
			return false;

		setBank(getBank() - amount);
		save();

		return true;
	}

	public boolean depositToBank(UUID target, float amount) {
		if (!isTownAdmin(target))
			return false;

		EconomyResponse request = TheNewTown.getInstance().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(target), amount);
		if (!request.transactionSuccess())
			return false;

		setBank(getBank() + amount);
		save();

		return true;
	}

	public long getWarPower() {
		return getPlots().size() * TheNewTown.getInstance().getLandMultiplier();
	}

	public boolean createWarGoal(String target, WarGoalObjective objective) {
		if (WarManager.getInstance().isAtWar(target) || WarManager.getInstance().isAtWar(getName()))
			return false;

		for (WarGoal goal : getActiveWarGoals())
			if (goal.getTarget().equalsIgnoreCase(target))
				return false;

		WarGoal goal = new WarGoal(getName(), target, objective, TownFile.open(), getName() + ".warGoals");
		getActiveWarGoals().add(goal);
		save();
		return true;
	}

	public void cancelWarGoal(long id) {
		for (WarGoal goal : new ArrayList<>(getActiveWarGoals())) {
			if (goal.getId() == id) {
				getActiveWarGoals().remove(goal);
				TownFile file = TownFile.open();
				file.getData().set(getName() + ".warGoals." + id, null);
				return;
			}
		}
	}

	/**
	 * Gets a list of all towns currently justifying a war goal against this town.
	 * */
	public List<Town> getTargetingTowns() {
		List<Town> targeting = new ArrayList<>();
		for (Town t : TownManager.getInstance().getTowns().values()) {
			for (WarGoal goal : t.getActiveWarGoals()) {
				if (goal.getTarget().equalsIgnoreCase(getName())) {
					targeting.add(t);
					break;
				}
			}
		}
		return targeting;
	}

	/**
	 * Starts a war given the war goal with the current id
	 * */
	public void startWar(long id) {
		throw new NotImplementedException();
	}

}
