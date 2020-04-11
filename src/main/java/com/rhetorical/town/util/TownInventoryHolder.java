package com.rhetorical.town.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class TownInventoryHolder implements InventoryHolder {

	private boolean shouldCancelClick;

	private Inventory prevPage;
	private Inventory nextPage;

	private TownMenuGroup menuGroup;

	private String townToJustifyAgaint;

	private String owner;

	public TownInventoryHolder(boolean shouldCancelClick, String town) {
		setShouldCancelClick(shouldCancelClick);
		setMenuGroup(TownMenuGroup.SINGLE);
		setOwner(town);
	}

	public TownInventoryHolder(boolean shouldCancelClick, String town, Inventory nextPage, Inventory prevPage, TownMenuGroup group) {
		setShouldCancelClick(shouldCancelClick);
		setNextPage(nextPage);
		setPrevPage(prevPage);
		setMenuGroup(group);
		setOwner(town);
	}

	public TownInventoryHolder(boolean shouldCancelClick, String town, String townToJustifyAgaint) {
		setShouldCancelClick(shouldCancelClick);
		setMenuGroup(TownMenuGroup.WAR_GOAL_CHOICE);
		setTownToJustifyAgaint(townToJustifyAgaint);
		setOwner(town);
	}


	private void setShouldCancelClick(boolean value) {
		shouldCancelClick = value;
	}

	public boolean isShouldCancelClick() {
		return shouldCancelClick;
	}

	@Override
	public Inventory getInventory() {
		return null;
	}

	public Inventory getPrevPage() {
		return prevPage;
	}

	public void setPrevPage(Inventory prevPage) {
		this.prevPage = prevPage;
	}

	public Inventory getNextPage() {
		return nextPage;
	}

	public void setNextPage(Inventory nextPage) {
		this.nextPage = nextPage;
	}

	public TownMenuGroup getMenuGroup() {
		return menuGroup;
	}

	public void setMenuGroup(TownMenuGroup menuGroup) {
		this.menuGroup = menuGroup;
	}

	public String getTownToJustifyAgaint() {
		return townToJustifyAgaint;
	}

	public void setTownToJustifyAgaint(String townToJustifyAgaint) {
		this.townToJustifyAgaint = townToJustifyAgaint;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
}
