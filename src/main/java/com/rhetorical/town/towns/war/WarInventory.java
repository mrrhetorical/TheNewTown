package com.rhetorical.town.towns.war;

import com.rhetorical.town.TheNewTown;
import com.rhetorical.town.towns.InventorySystem;
import com.rhetorical.town.towns.Town;
import com.rhetorical.town.towns.TownInventory;
import com.rhetorical.town.towns.TownManager;
import com.rhetorical.town.util.TownInventoryHolder;
import com.rhetorical.town.util.TownMenuGroup;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WarInventory implements Listener, InventorySystem {

	private String town;

	private Inventory menu; // Main menu
	private Inventory townList; // paged, used in the war goal creation menu
	private Inventory warObjectiveList; // used to select war objective. Town data stored in header
	private Inventory warGoals; // paged, used in war start menu
	private Inventory currentWar;

	private ItemStack back, next, prev, close;

	public WarInventory(String town) {
		setTown(town);

		// setup back item
		{
			back = new ItemStack(Material.REPEATER);
			ItemMeta meta = back.getItemMeta();
			meta.setDisplayName(ChatColor.RED + "Back");
			back.setItemMeta(meta);
		}

		// setup next item
		{
			next = new ItemStack(Material.ARROW);
			ItemMeta meta = next.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "Next");
			next.setItemMeta(meta);
		}

		// setup prev item
		{
			prev = new ItemStack(Material.ARROW);
			ItemMeta meta = prev.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "Prev");
			prev.setItemMeta(meta);
		}

		// setup close item
		{
			close = new ItemStack(Material.COMPARATOR);
			ItemMeta meta = close.getItemMeta();
			meta.setDisplayName(ChatColor.RED + "Close");
			close.setItemMeta(meta);
		}

		Bukkit.getServer().getPluginManager().registerEvents(this, TheNewTown.getInstance());

		setupMenu();
	}

	private void setupMenu() {
		menu = Bukkit.createInventory(new TownInventoryHolder(true), 27, ChatColor.BLUE + "" + ChatColor.BOLD + "War Menu");

		ItemStack ongoing = new ItemStack(Material.BOOK);
		{
			ItemMeta meta = ongoing.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Ongoing War");
			List<String> lore = new ArrayList<>();
			lore.add("Under Construction!");
			meta.setLore(lore);
			ongoing.setItemMeta(meta);
		}

		ItemStack warGoals = new ItemStack(Material.BOOK);
		{
			ItemMeta meta = warGoals.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Current War Goals");
			warGoals.setItemMeta(meta);
		}

		ItemStack justify = new ItemStack(Material.BOOK);
		{
			ItemMeta meta = justify.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Justify War Goal");
			justify.setItemMeta(meta);
		}

		menu.setItem(11, ongoing);
		menu.setItem(13, warGoals);
		menu.setItem(15, justify);
		menu.setItem(26, back);
	}

	private void setupCurrentWar() {
		throw new NotImplementedException();
	}

	private void updateWarGoals() {
		Town t = TownManager.getInstance().getTown(getTown());


		Inventory prevMenu = null;

		int pages = (int) Math.ceil(t.getActiveWarGoals().size() / 27f);

		pages = pages == 0 ? 1 : pages;

		for (int i = 0; i < pages; i++) {
			Inventory inv = Bukkit.createInventory(new TownInventoryHolder(true, null, prevMenu, TownMenuGroup.COMPLETED_WAR_GOAL_LIST), 36, ChatColor.BLUE + "" + ChatColor.BOLD + "War Goals - (" + (i + 1) + "/" + pages + ")");

			if (prevMenu != null)
				if (prevMenu.getHolder() instanceof TownInventoryHolder) {
					((TownInventoryHolder) prevMenu.getHolder()).setNextPage(inv);
				}

			prevMenu = inv;
			List<WarGoal> goals = t.getActiveWarGoals();

			for (int k = i * 27; k < i * 27 + 27 && k < goals.size(); k++) {
				WarGoal goal = goals.get(k);
				ItemStack item = new ItemStack(Material.BOOK);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + goal.getId());
				List<String> lore = new ArrayList<>();
				lore.add(ChatColor.YELLOW + String.format("WarGoal against %s", goal.getTarget()));
				int h, m;
				if (!goal.isCompleted()) {
					m = (int) LocalDateTime.now().until(goal.getCompletionDate(), ChronoUnit.MINUTES);
					h = m % 60;
					m /= 60;
					lore.add(ChatColor.YELLOW + String.format("Time Left: %sh%sm", h, m));
					lore.add(ChatColor.YELLOW + "[RMB] Cancel Justification");
				} else {
					m = (int) LocalDateTime.now().until(goal.getExpiryDate(), ChronoUnit.MINUTES);
					h = m % 60;
					m /= 60;
					lore.add(ChatColor.YELLOW + String.format("Expires in: %sh%sm", h, m));
					lore.add(ChatColor.YELLOW + "[LMB] Declare War!");
				}
				meta.setLore(lore);
				item.setItemMeta(meta);

				inv.addItem(item);
			}

			if (i != 0)
				inv.setItem(30, prev);

			if (i != pages - 1)
				inv.setItem(32, next);

			inv.setItem(31, back);

			if (i == 0)
				warGoals = inv;
		}
	}

	@SuppressWarnings("Duplicates")
	private void updateTownList() {
		Town t = TownManager.getInstance().getTown(getTown());

		Inventory prevMenu = null;

		List<Town> towns = new ArrayList<>(TownManager.getInstance().getTowns().values());

		for (Town town : new ArrayList<>(towns))
			if (t.getAllies().contains(town.getName()) || t.getName().equalsIgnoreCase(town.getName()))
				towns.remove(town);

		int pages = (int) Math.ceil(towns.size() / 27f);

		pages = pages == 0 ? 1 : pages;


		for (int i = 0; i < pages; i++) {
			Inventory inv = Bukkit.createInventory(new TownInventoryHolder(true, null, prevMenu, TownMenuGroup.JUSTIFY_WAR_TOWN_LIST), 36, ChatColor.BLUE + "" + ChatColor.BOLD + "Justify War Goal - (" + (i + 1) + "/" + pages + ")");

			if (prevMenu != null)
				if (prevMenu.getHolder() instanceof TownInventoryHolder) {
					((TownInventoryHolder) prevMenu.getHolder()).setNextPage(inv);
				}

			prevMenu = inv;


			for (int k = i * 27; k < i * 27 + 27 && k < towns.size(); k++) {
				ItemStack item = new ItemStack(Material.GRASS_BLOCK);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + towns.get(k).getName());
				List<String> lore = new ArrayList<>();
				lore.add(ChatColor.YELLOW + "[LMB] Begin justifying war goal");
				meta.setLore(lore);
				item.setItemMeta(meta);

				inv.addItem(item);
			}

			if (i != 0)
				inv.setItem(30, prev);

			if (i != pages - 1)
				inv.setItem(32, next);

			inv.setItem(31, back);

			if (i == 0)
				townList = inv;
		}
	}

	private boolean shouldCancelClick(Inventory inventory) {
		InventoryHolder holder = inventory.getHolder();
		if (!(holder instanceof TownInventoryHolder))
			return false;
		return ((TownInventoryHolder) holder).isShouldCancelClick();
	}

	public void unregister() {
		HandlerList.unregisterAll(this);
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public Inventory getMenu() {
		return menu;
	}

	public Inventory getTownList() {
		return townList;
	}

	public Inventory getWarObjectiveList() {
		return warObjectiveList;
	}

	public Inventory getWarGoals() {
		return warGoals;
	}

	public Inventory getCurrentWar() {
		return currentWar;
	}

	@EventHandler
	@SuppressWarnings("Duplicates")
	public void onClick(InventoryClickEvent e) {
		if (!shouldCancelClick(e.getInventory()))
			return;

		e.setCancelled(true);


		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
			return;

		ItemStack clicked = e.getInventory().getItem(e.getRawSlot());

		Player p = (Player) e.getWhoClicked();

		Town town = TownManager.getInstance().getTown(getTown());

		if(clicked.equals(back)) {
			if (e.getInventory().equals(getMenu())) {
				town.getInventory().openMenu(p);
				return;
			} else if (e.getInventory().equals(getCurrentWar())) {
				p.openInventory(getMenu());
			}
		}

		int slot = e.getRawSlot();

		if (e.getInventory().equals(getMenu())) {
			if (slot == 11) {
				setupCurrentWar();
				if (getCurrentWar() != null)
					p.openInventory(getCurrentWar());
			} else if (slot == 13) {
				updateWarGoals();
				p.openInventory(getWarGoals());
			} else if (slot == 15) {
				updateTownList();
				p.openInventory(getTownList());
			}
		}

		TownInventoryHolder holder = null;

		if (e.getInventory().getHolder() instanceof TownInventoryHolder)
			holder = (TownInventoryHolder) e.getInventory().getHolder();

		if (holder != null) {
			if (holder.getMenuGroup() == TownMenuGroup.COMPLETED_WAR_GOAL_LIST) {
				if (clicked.equals(prev)) {
					if (holder.getPrevPage() != null)
						p.openInventory(holder.getPrevPage());
				} else if (clicked.equals(next)) {
					if (holder.getNextPage() != null)
						p.openInventory(holder.getNextPage());
				} else if (clicked.equals(back)) {
					p.openInventory(getMenu());
				}
			} else if (holder.getMenuGroup() == TownMenuGroup.JUSTIFY_WAR_TOWN_LIST) {
				if (clicked.equals(prev)) {
					if (holder.getPrevPage() != null)
						p.openInventory(holder.getPrevPage());
				} else if (clicked.equals(next)) {
					if (holder.getNextPage() != null)
						p.openInventory(holder.getNextPage());
				} else if (clicked.equals(back)) {
					p.openInventory(getMenu());
				}
			}

		}
	}
}
