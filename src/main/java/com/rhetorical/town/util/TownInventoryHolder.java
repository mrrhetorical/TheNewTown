package com.rhetorical.town.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class TownInventoryHolder implements InventoryHolder {

	private boolean shouldCancelClick;

	private Inventory prevPage;
	private Inventory nextPage;

	private TownMenuGroup menuGroup;

	public TownInventoryHolder(boolean shouldCancelClick) {
		setShouldCancelClick(shouldCancelClick);
		setMenuGroup(TownMenuGroup.SINGLE);
	}

	public TownInventoryHolder(boolean shouldCancelClick, Inventory nextPage, Inventory prevPage, TownMenuGroup group) {
		setShouldCancelClick(shouldCancelClick);
		setNextPage(nextPage);
		setPrevPage(prevPage);
		setMenuGroup(group);
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
}
