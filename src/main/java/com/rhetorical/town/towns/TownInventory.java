package com.rhetorical.town.towns;

import com.rhetorical.town.TheNewTown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
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
		return (invite != null && invite.contains(inventory)) || (members != null && members.contains(inventory)) || (menu != null && menu.equals(inventory)) || (infoMenu != null && infoMenu.equals(inventory)) || (flags != null && flags.equals(inventory));
	}

	public void setupMenu() {
		menu = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Town " + ChatColor.YELLOW + getTown());

		ItemStack info = new ItemStack(Material.PAPER);
		ItemMeta infoMeta = info.getItemMeta();
		infoMeta.setDisplayName(ChatColor.BLUE + "Town Information");
		info.setItemMeta(infoMeta);


		ItemStack invite = new ItemStack(Material.SKELETON_SKULL);
		ItemMeta inviteMeta = invite.getItemMeta();
		inviteMeta.setDisplayName(ChatColor.YELLOW + "Invite Players");
		invite.setItemMeta(inviteMeta);

		ItemStack flagsItem = new ItemStack(Material.RED_BANNER);
		BannerMeta meta = (BannerMeta) flagsItem.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Town Flags");
		meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_SMALL));
		meta.addPattern(new Pattern(DyeColor.BLUE, PatternType.SQUARE_TOP_LEFT));
		flagsItem.setItemMeta(meta);


		getMenu().setItem(11, info);
		getMenu().setItem(13, invite);
		getMenu().setItem(15, flagsItem);
		getMenu().setItem(26, close);
	}

	public void setupInfoMenu() {
		infoMenu = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Town " + ChatColor.YELLOW + getTown() + " - Information");

		Town town = TownManager.getInstance().getTown(getTown());
		if (town == null)
			return;

		ItemStack nameItem = new ItemStack(Material.NAME_TAG);
		ItemMeta nameMeta = nameItem.getItemMeta();
		nameMeta.setDisplayName(ChatColor.GOLD + "Name: " + ChatColor.YELLOW + getTown());
		nameItem.setItemMeta(nameMeta);

		ItemStack populationItem = new ItemStack(Material.POPPY);
		ItemMeta populationMeta = populationItem.getItemMeta();
		populationMeta.setDisplayName(ChatColor.BLUE + "Population: " + ChatColor.YELLOW + town.getResidents().size());
		populationItem.setItemMeta(populationMeta);

		ItemStack sizeItem = new ItemStack(Material.CRAFTING_TABLE);
		ItemMeta sizeMeta = sizeItem.getItemMeta();
		sizeMeta.setDisplayName(ChatColor.RED + "Size: " + ChatColor.YELLOW + town.getTownType().getReadable() + " " + ChatColor.WHITE + "(" + ChatColor.YELLOW + town.getPlots().size() + ChatColor.WHITE + ")");
		sizeItem.setItemMeta(sizeMeta);

		ItemStack membersItem = new ItemStack(Material.SKELETON_SKULL);
		ItemMeta membersMeta = membersItem.getItemMeta();
		membersMeta.setDisplayName(ChatColor.YELLOW + "Member List");
		membersItem.setItemMeta(membersMeta);


		getInfoMenu().setItem(10, nameItem);
		getInfoMenu().setItem(12, populationItem);
		getInfoMenu().setItem(14, sizeItem);
		getInfoMenu().setItem(16, membersItem);
		getInfoMenu().setItem(22, back);
	}

	public void setupFlagsMenu() {
		flags = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Town " + ChatColor.YELLOW + getTown() + " - Flags");

		getFlags().setItem(22, back);
	}

	public void setupInviteMenu() {
		//todo: finish
	}

	public void setupMembersMenu() {
		//todo: finish
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

		ItemStack clicked = e.getInventory().getItem(e.getRawSlot());

		Player p = (Player) e.getWhoClicked();

		if (clicked.equals(close)) {
			p.closeInventory();
			return;
		} else if(clicked.equals(back)) {
			if (e.getInventory().equals(getInfoMenu())) {
				openMenu(p);
				return;
			} else if (invite.contains(e.getInventory())) {
				openMenu(p);
				return;
			} else if (members.contains(e.getInventory())) {
				openInfoMenu(p);
				return;
			}
		}

		//is menu
		if (e.getInventory().equals(getMenu())) {
			if (e.getRawSlot() == 11) {
				openInfoMenu((Player) e.getWhoClicked());
			}
		}

	}

}
