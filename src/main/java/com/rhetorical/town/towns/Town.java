package com.rhetorical.town.towns;

import com.rhetorical.town.files.TownFile;
import org.bukkit.configuration.ConfigurationSection;

import java.time.DateTimeException;
import java.time.LocalDateTime;
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

		int size = getPlots().size();

		if (size > TownType.MAJOR_CITY.getMaxPlots())
			townType = TownType.CITY_STATE;
		else if (size > TownType.CITY.getMaxPlots())
			townType = TownType.MAJOR_CITY;
		else if (size > TownType.TOWN.getMaxPlots())
			townType = TownType.CITY;
		else if (size > TownType.VILLAGE.getMaxPlots())
			townType = TownType.TOWN;
		else if (size > TownType.HAMLET.getMaxPlots())
			townType = TownType.VILLAGE;
		else
			townType = TownType.HAMLET;

		List<String> residents = file.getData().getStringList(name + ".residents");
		for (String key : residents) {
			UUID player = UUID.fromString(key);
			getResidents().add(player);
		}

		tax = (float) file.getData().getDouble(name + ".tax");
	}

	/**
	 * Used in town creation & initialization.
	 * */
	public Town(UUID mayor, Plot initial, String name) {
		setMayor(mayor);
		plots.add(initial);
		setTax(0f);
		residents.add(mayor);
		setName(name);
		setTownType(TownType.HAMLET);

		save();
	}

	public void save() {

		TownFile file = TownFile.open();

		file.getData().set(getName() + ".residents", "");
		List<String> r = new ArrayList<>();
		for (UUID resident : getResidents())
			r.add(resident.toString());

		file.getData().set(getName() + ".residents", r);

		file.getData().set(getName() + ".mayor", getMayor().toString());

		file.getData().set(getName() + ".tax", getTax());

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

	public TownType getTownType() {
		return townType;
	}

	public void setTownType(TownType value) {
		townType = value;
	}

}
