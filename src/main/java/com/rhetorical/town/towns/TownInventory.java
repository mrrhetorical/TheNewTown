package com.rhetorical.town.towns;

import com.rhetorical.town.TheNewTown;
import com.rhetorical.town.towns.flags.TownFlag;
import com.rhetorical.town.towns.invite.InviteManager;
import org.bukkit.*;
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
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

		flags = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Town " + ChatColor.YELLOW + getTown() + " - Flags");
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

		Town t = TownManager.getInstance().getTown(getTown());

		for (int i = 0; i < TownFlag.values().length; i++) {
			TownFlag flag = TownFlag.values()[i];
			boolean status = t.getFlag(flag);

			ItemStack item = new ItemStack(status ? Material.GREEN_BANNER : Material.RED_BANNER);
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				meta.setDisplayName((status ? ChatColor.GREEN : ChatColor.RED) + flag.getName());
				List<String> lore = new ArrayList<>();
				lore.add("" + ChatColor.YELLOW + ChatColor.ITALIC + flag.getDescription());
				lore.add(ChatColor.WHITE + "Status: " + (status ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
				meta.setLore(lore);
			}
			item.setItemMeta(meta);

			getFlags().setItem(i, item);
		}

		getFlags().setItem(22, back);
	}

	public void setupInviteMenu() {

		invite.clear();

		List<Player> poss = new ArrayList<>(Bukkit.getOnlinePlayers());
		Town t = TownManager.getInstance().getTown(getTown());
		for (Player p : new ArrayList<>(poss))
			if (t.getResidents().contains(p.getUniqueId()))
				poss.remove(p);

		int numPages =  (int) Math.ceil((float) poss.size() / 27f);
		for (int i = 0; i < numPages; i++) {
			Inventory inv = Bukkit.createInventory(null, 36, ChatColor.GOLD + "Invite Players - " + (i + 1));

			for(int k = 0; k < 27 && !poss.isEmpty(); k++) {
				ItemStack skull = new ItemStack(Material.SKELETON_SKULL);
				SkullMeta meta = (SkullMeta) skull.getItemMeta();
				if (meta != null) {
					meta.setOwningPlayer(poss.get(0));
					meta.setDisplayName(ChatColor.YELLOW + poss.get(i).getDisplayName());
					List<String> lore = new ArrayList<>();
					lore.add(ChatColor.GREEN + "[Click to Invite]");
					meta.setLore(lore);
					poss.remove(0);
				}
				skull.setItemMeta(meta);
				inv.addItem(skull);
			}

			if (i != 0) {
				inv.setItem(30, prev);
			}

			if (i != numPages - 1) {
				inv.setItem(32, next);
			}

			inv.setItem(31, back);

			invite.add(inv);
		}
	}

	public void setupMembersMenu() {

		members.clear();

		List<UUID> poss = new ArrayList<>(TownManager.getInstance().getTown(getTown()).getResidents());

		int numPages =  (int) Math.ceil((float) poss.size() / 27f);
		for (int i = 0; i < numPages; i++) {
			Inventory inv = Bukkit.createInventory(null, 36, ChatColor.GOLD + "Invite Players - " + (i + 1));

			for(int k = 0; k < 27 && !poss.isEmpty(); k++) {
				ItemStack skull = new ItemStack(Material.SKELETON_SKULL);
				SkullMeta meta = (SkullMeta) skull.getItemMeta();
				if (meta != null) {
					meta.setOwningPlayer(Bukkit.getOfflinePlayer(poss.get(0)));
					meta.setDisplayName(ChatColor.YELLOW + Bukkit.getOfflinePlayer(poss.get(0)).getName());
					List<String> lore = new ArrayList<>();
					lore.add(ChatColor.RED + "[Click to Kick]");
					meta.setLore(lore);
					poss.remove(0);
				}
				skull.setItemMeta(meta);
				inv.addItem(skull);
			}

			if (i != 0) {
				inv.setItem(30, prev);
			}

			if (i != numPages - 1) {
				inv.setItem(32, next);
			}

			inv.setItem(31, back);

			members.add(inv);
		}
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
			} else if (e.getInventory().equals(getFlags())) {
				openMenu(p);
				return;
			}
		}

		//is menu
		if (e.getInventory().equals(getMenu())) {
			if (e.getRawSlot() == 11) {
				openInfoMenu((Player) e.getWhoClicked());
			} else if (e.getRawSlot() == 13) {
				setupInviteMenu();
				if (!invite.isEmpty())
					e.getWhoClicked().openInventory(invite.get(0));
			} else if (e.getRawSlot() == 15) {
				setupFlagsMenu();
				p.openInventory(getFlags());
			}

		}

		if (e.getInventory().equals(getInfoMenu())) {
			if (e.getRawSlot() == 16) {
				setupMembersMenu();
				if (!members.isEmpty())
					p.openInventory(members.get(0));
			}
		}

		//is invite menu
		if (invite.contains(e.getInventory())) {
			if (e.getCurrentItem() == null)
				return;

			ItemStack item = e.getCurrentItem();
			if (item.equals(prev)) {
				p.openInventory(invite.get(invite.indexOf(e.getInventory()) - 1));
			} else if (item.equals(next)) {
				p.openInventory(invite.get(invite.indexOf(e.getInventory()) + 1));
			}

			if (item.getItemMeta() instanceof SkullMeta) {
				if (TownManager.getInstance().getTown(getTown()).getMayor().equals(p.getUniqueId())) {
					SkullMeta meta = (SkullMeta) item.getItemMeta();
					InviteManager.getInstance().generateRequest(e.getWhoClicked().getUniqueId(), meta.getOwningPlayer().getUniqueId(), TownManager.getInstance().getTownOfPlayer(e.getWhoClicked().getUniqueId()).getName());
					openMenu(p);
				} else {
					p.sendMessage(ChatColor.RED + "You must be the mayor of a town to invite players!");
				}
			}
		}

		//is member menu
		if (members.contains(e.getInventory())) {
			ItemStack item = e.getCurrentItem();
			if (item.equals(prev)) {
				p.openInventory(members.get(members.indexOf(e.getInventory()) - 1));
			} else if (item.equals(next)) {
				p.openInventory(members.get(members.indexOf(e.getInventory()) + 1));
			}

			if (item.getItemMeta() instanceof SkullMeta) {
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				Town t = TownManager.getInstance().getTown(getTown());
				if (t.getMayor().equals(p.getUniqueId())) {
					if (!t.getMayor().equals(meta.getOwningPlayer().getUniqueId())) {
						if (t.removePlayer(meta.getOwningPlayer().getUniqueId())) {
							p.sendMessage(ChatColor.GREEN + "Successfully kicked player from your town!");
							t.save();
						} else {
							p.sendMessage(ChatColor.RED + "Could not kick player from your town!");
						}
					} else
						p.sendMessage(ChatColor.RED + "You cannot kick yourself from your town!");
					openMenu(p);
				} else {
					p.sendMessage(ChatColor.RED + "You must be the mayor of a town to kick players!");
				}
			}
		}

		//is flags menu
		if (e.getInventory().equals(getFlags())) {
			int slot = e.getRawSlot();
			TownFlag flag;

			if (slot >= TownFlag.values().length)
				return;

			flag = TownFlag.values()[slot];

			Town town = TownManager.getInstance().getTown(getTown());
			town.setFlag(flag, !town.getFlag(flag));
			setupFlagsMenu();
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 1.0f, 0.4f);
		}

	}

}
