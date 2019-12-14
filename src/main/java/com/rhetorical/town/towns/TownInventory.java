package com.rhetorical.town.towns;

import com.rhetorical.town.TheNewTown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TownInventory implements Listener {

	private String town;
	private Inventory menu, infoMenu, flags;
	private List<Inventory> invite = new ArrayList<>();
	private List<Inventory> members = new ArrayList<>();

	private ItemStack back, next, prev, close;

	TownInventory(String town) {
		setTown(town);
		Bukkit.getPluginManager().registerEvents(this, TheNewTown.getInstance());

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

		setupMenu();
	}

	public void unregister() {
		menu = null;
		infoMenu = null;
		flags = null;
		invite.clear();
		members.clear();
		back = null;
		next = null;
		prev = null;
		close = null;
		HandlerList.unregisterAll(this);
	}

	public String getTown() {
		return town;
	}

	public void setTown(String value) {
		town = value;
	}

	public Inventory getMenu() {
		return menu;
	}

	public Inventory getInfoMenu() {
		return infoMenu;
	}

	public Inventory getFlags() {
		return flags;
	}

	public Inventory getInviteInventory(int page) {
		return invite.get(page);
	}

	public Inventory getMembersInventory(int page) {
		return members.get(page);
	}

	private boolean shouldCancelClick(Inventory inventory) {
		return invite.contains(inventory) || members.contains(inventory) || menu.equals(inventory) || infoMenu.equals(inventory) || flags.equals(inventory);
	}

	public void setupMenu() {
		menu = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Town " + ChatColor.YELLOW + getTown());

		getMenu().setItem(26, close);
	}

	public void setupInfoMenu() {
		infoMenu = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Town " + ChatColor.YELLOW + getTown() + " - Information");

		getInfoMenu().setItem(22, back);
	}

	public void setupFlagsMenu() {
		flags = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Town " + ChatColor.YELLOW + getTown() + " - Flags");

		getFlags().setItem(22, back);
	}

	public void openMenu(Player p) {
		p.openInventory(getMenu());
	}

	public void openInfoMenu(Player p) {
		setupInfoMenu();
		p.openInventory(getInfoMenu());
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!shouldCancelClick(e.getInventory()))
			return;

		e.setCancelled(true);


		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
			return;

		ItemStack clicked = e.getCurrentItem();

		Player p = (Player) e.getWhoClicked();

		if (clicked.equals(close)) {
			p.closeInventory();
			return;
		} else if(clicked.equals(back)) {
			if (getInfoMenu() != null && getInfoMenu().equals(e.getInventory())) {
				openInfoMenu(p);
				return;
			} else if (invite.contains(e.getInventory())) {
				openMenu(p);
				return;
			} else if (members.contains(e.getInventory())) {
				openInfoMenu(p);
				return;
			}
		}

	}

}
