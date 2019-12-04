package com.rhetorical.town.towns;

import com.rhetorical.town.TheNewTown;
import com.rhetorical.town.files.TownFile;
import javafx.util.converter.LocalDateTimeStringConverter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.ConfigurationSection;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Town {

	private UUID mayor;
	private String name;

	private List<Plot> plots = new ArrayList<>();

	private List<UUID> residents = new ArrayList<>();

	private TownType townType;

	private float tax = 0f;

	private LocalDateTime lastTaxPeriod;
	private LocalDateTime lastUpkeepPeriod;

	/**
	 * Used in town loading.
	 * */
	@SuppressWarnings("")
	public Town(String name) {
		TownFile file = TownFile.open();

		if (!file.getData().contains(name))
			return;

		setName(name);

		String m = file.getData().getString(name + ".mayor");
		if (m == null)
			return;
		setMayor(UUID.fromString(m));

//		ConfigurationSection plots = file.getData().getConfigurationSection(name + ".plots");
//		for (String key : plots.getKeys(false)) {
//			long id;
//			try {
//				id = Long.parseLong(key);
//			} catch (Exception ignored) { continue; }
//			Plot plot = Plot.loadPlot(name, id, file);
//			if (plot == null)
//				continue;
//			getPlots().add(plot);
//		}
//
//		townType = calculateTownSize(getPlots().size());

		List<String> residents = file.getData().getStringList(name + ".residents");
		for (String key : residents) {
			UUID player = UUID.fromString(key);
			getResidents().add(player);
		}

		tax = (float) file.getData().getDouble(name + ".tax");

		LocalDateTimeStringConverter converter = new LocalDateTimeStringConverter();

		lastTaxPeriod = converter.fromString(file.getData().getString(getName() + ".lastTaxPeriod"));
		lastUpkeepPeriod = converter.fromString(file.getData().getString(getName() + ".lastUpkeepPeriod"));
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
		setTownType(TownType.HAMLET);
		if (!addPlot(initial)) {
			PlotAlreadyExistsException.FailReason reason;
			if (!TownManager.getInstance().isChunkClaimed(initial))
				reason = PlotAlreadyExistsException.FailReason.ALREADY_CLAIMED;
			else
				reason = PlotAlreadyExistsException.FailReason.REGION_PROTECTION;

				throw new PlotAlreadyExistsException(initial, reason);
		}
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

		LocalDateTimeStringConverter converter = new LocalDateTimeStringConverter();

		file.getData().set(getName() + ".lastTaxPeriod", converter.toString(lastTaxPeriod));

		file.getData().set(getName() + ".lastUpkeepPeriod", converter.toString(lastUpkeepPeriod));

		file.getData().set(getName() + ".plots", null);

		for (Plot plot : getPlots()) {
			plot.save(getName(), file);
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

	public boolean addPlot(Chunk chunk) {
		if (TownManager.getInstance().isChunkClaimed(chunk))
			return false;

		//todo: world guard checks to make sure plot doesn't overlap region

		Plot plot = new Plot(generatePlotId(), getMayor(), null, chunk, false, 0f, getName());
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

	public boolean addPlayer(UUID target) {
		if (getResidents().contains(target))
			return false;

		getResidents().add(target);
		return true;
	}

	void delete() {
		for (Plot plot : getPlots()) {
			plot.unregister();
		}
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

			EconomyResponse r = TheNewTown.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(getMayor()), collected);
			if (!r.transactionSuccess())
				Bukkit.getLogger().severe("Could not deposit money into mayor of " + getName() + "'s account!");
		}

		if (ChronoUnit.HOURS.between(now, lastUpkeepPeriod) >= TownManager.getInstance().getUpkeepPeriod()) {
			lastUpkeepPeriod = now;

			EconomyResponse tax = TheNewTown.getInstance().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(getMayor()), (double) TownManager.getInstance().getUpkeep(this));
			if (!tax.transactionSuccess())
				demote();

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

}
