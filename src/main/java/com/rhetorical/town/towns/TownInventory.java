package com.rhetorical.town.towns;

import com.rhetorical.town.TheNewTown;
import com.rhetorical.town.towns.flags.TownFlag;
import com.rhetorical.town.towns.invite.InviteManager;
import com.rhetorical.town.util.TownInventoryHolder;
import com.rhetorical.town.util.TownMenuGroup;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class TownInventory implements Listener, InventorySystem {

	private String town;
	private Inventory menu, infoMenu, flags;
	private Inventory invite;
	private Inventory members;

	private ItemStack back, next, prev, close;

	private Map<Plot, Inventory> plotFlagInventories = new HashMap<>();

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
		if (invite != null)
			invite.clear();
		if (members != null)
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

	public Inventory getInviteInventory() {
		return invite;
	}

	public Inventory getMembersInventory() {
		return members;
	}

	public Map<Plot, Inventory> getPlotFlagInventories() {
		return plotFlagInventories;
	}

	private boolean shouldCancelClick(Inventory inventory) {
		InventoryHolder holder = inventory.getHolder();
		if (!(holder instanceof TownInventoryHolder))
			return false;
		return ((TownInventoryHolder) holder).isShouldCancelClick();
	}

	public void setupMenu() {
		menu = Bukkit.createInventory(new TownInventoryHolder(true, getTown()), 27, ChatColor.BLUE + "" + ChatColor.BOLD + getTown());

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

		ItemStack war = new ItemStack(Material.IRON_SWORD);
		ItemMeta warMeta = war.getItemMeta();
		warMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "War Menu");
		war.setItemMeta(warMeta);

		getMenu().setItem(10, info);
		getMenu().setItem(12, war);
		getMenu().setItem(14, invite);
		getMenu().setItem(16, flagsItem);
		getMenu().setItem(22, close);

		flags = Bukkit.createInventory(new TownInventoryHolder(true, getTown()), 27, ChatColor.BLUE + "" + ChatColor.BOLD + getTown() + " - Town-wide Flags");
	}

	public void setupInfoMenu() {
		infoMenu = Bukkit.createInventory(new TownInventoryHolder(true, getTown()), 27, ChatColor.BLUE + "" + ChatColor.BOLD + getTown() + " - Stats");

		Town town = TownManager.getInstance().getTown(getTown());
		if (town == null)
			return;

//		ItemStack nameItem = new ItemStack(Material.NAME_TAG);
//		ItemMeta nameMeta = nameItem.getItemMeta();
//		nameMeta.setDisplayName(ChatColor.GOLD + "Name: " + ChatColor.YELLOW + getTown());
//		nameItem.setItemMeta(nameMeta);

		ItemStack populationItem = new ItemStack(Material.POPPY);
		ItemMeta populationMeta = populationItem.getItemMeta();
		populationMeta.setDisplayName(ChatColor.BLUE + "Population: " + ChatColor.YELLOW + town.getResidents().size());
		populationItem.setItemMeta(populationMeta);

		ItemStack sizeItem = new ItemStack(Material.CRAFTING_TABLE);
		ItemMeta sizeMeta = sizeItem.getItemMeta();
		sizeMeta.setDisplayName(ChatColor.RED + "Size: " + ChatColor.YELLOW + town.getTownType().getReadable() + " " + ChatColor.WHITE + "(" + ChatColor.YELLOW + town.getPlots().size() + ChatColor.WHITE + ")");
		sizeItem.setItemMeta(sizeMeta);

		ItemStack financials = new ItemStack(Material.GOLD_NUGGET);
		ItemMeta financialMeta = financials.getItemMeta();
		financialMeta.setDisplayName("" + ChatColor.GOLD + ChatColor.BOLD + "Finances");
		List<String> financialLore = new ArrayList<>();

		float nextUpkeep = TownManager.getInstance().getUpkeep(town);
		nextUpkeep = Math.round(nextUpkeep * 100f) / 100f;

		financialLore.add(ChatColor.YELLOW + "Treasury balance: " + (town.getBank() > nextUpkeep ? ChatColor.GRAY : ChatColor.RED) + "$" + town.getBank());
		financialLore.add(ChatColor.YELLOW + "Next Upkeep Bill: " + ChatColor.GRAY + "$" + nextUpkeep);
		financialLore.add(ChatColor.YELLOW + "Tax Rate: " + ChatColor.GRAY + "$" + town.getTax() + " / tax period");
		float expectedRevenue = (town.getResidents().size() - 1) * town.getTax();
		for (Plot plot : town.getPlots())
			if (plot.getLeaser() != null)
				expectedRevenue += plot.getCost();

		expectedRevenue = Math.round(expectedRevenue * 100f) / 100f;
		financialLore.add(ChatColor.YELLOW + "Expected Tax Revenue: " + (expectedRevenue >= nextUpkeep ? ChatColor.GREEN : ChatColor.GRAY) + "$" + expectedRevenue);

		//tax period stuff

		LocalDateTime now = LocalDateTime.now();

		LocalDateTime nextTaxDate = town.getLastTaxPeriod().plusHours(TownManager.getInstance().getTaxPeriod());
		LocalDateTime nextUpkeepDate = town.getLastUpkeepPeriod().plusHours(TownManager.getInstance().getUpkeepPeriod());

//		DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd hh:mm a O"); // 06/26 3:00pm GMT+&

		DateTimeFormatter format = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT);

		financialLore.add(ChatColor.YELLOW + "Next Upkeep Date: " + ChatColor.GRAY + nextUpkeepDate.format(format));
		financialLore.add(ChatColor.YELLOW + "Next Tax Date: " + ChatColor.GRAY + nextTaxDate.format(format));

		//end tax period stuff

		financialMeta.setLore(financialLore);
		financials.setItemMeta(financialMeta);

		ItemStack membersItem = new ItemStack(Material.SKELETON_SKULL);
		ItemMeta membersMeta = membersItem.getItemMeta();
		membersMeta.setDisplayName(ChatColor.YELLOW + "Member List");
		membersItem.setItemMeta(membersMeta);


		getInfoMenu().setItem(11, financials);
		getInfoMenu().setItem(13, sizeItem);
		getInfoMenu().setItem(15, membersItem);
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

		invite = null;
		//todo: possible GC required on inventory prev and next links

		List<Player> poss = new ArrayList<>(Bukkit.getOnlinePlayers());
		Town t = TownManager.getInstance().getTown(getTown());
		for (Player p : new ArrayList<>(poss))
			if (t.getResidents().contains(p.getUniqueId()))
				poss.remove(p);

		int numPages =  (int) Math.ceil((float) poss.size() / 27f);

		Inventory prevPage = null;

		numPages = numPages == 0 ? 1 : numPages;

		for (int i = 0; i < numPages; i++) {
			Inventory inv = Bukkit.createInventory(new TownInventoryHolder(true, getTown(), null, prevPage, TownMenuGroup.INVITE), 36, ChatColor.BLUE + "" + ChatColor.BOLD + "Invite Players - (" + (i + 1) + "/" + numPages + ")");

			if (prevPage != null)
				if (prevPage.getHolder() instanceof TownInventoryHolder) {
					((TownInventoryHolder) prevPage.getHolder()).setNextPage(inv);
				}

			prevPage = inv;

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

			if (i == 0)
				invite = inv;
		}
	}

	public void setupMembersMenu() {

		members = null;
		//todo: same possible GC memory leak as above

		List<UUID> poss = new ArrayList<>(TownManager.getInstance().getTown(getTown()).getResidents());

		Inventory prevPage = null;

		int numPages =  (int) Math.ceil((float) poss.size() / 27f);

		numPages = numPages == 0 ? 1 : numPages;

		for (int i = 0; i < numPages; i++) {
			Inventory inv = Bukkit.createInventory(new TownInventoryHolder(true, getTown(), null, prevPage, TownMenuGroup.MEMBER_LIST), 36, ChatColor.BLUE + "" + ChatColor.BOLD + "Member List - (" + (i + 1) + "/" + numPages + ")");

			if (prevPage != null)
				if (prevPage.getHolder() instanceof TownInventoryHolder) {
					((TownInventoryHolder) prevPage.getHolder()).setNextPage(inv);
				}

			prevPage = inv;

			for(int k = 0; k < 27 && !poss.isEmpty(); k++) {
				ItemStack skull = new ItemStack(Material.SKELETON_SKULL);
				SkullMeta meta = (SkullMeta) skull.getItemMeta();
				if (meta != null) {
					meta.setOwningPlayer(Bukkit.getOfflinePlayer(poss.get(0)));
					meta.setDisplayName(ChatColor.YELLOW + Bukkit.getOfflinePlayer(poss.get(0)).getName());
					List<String> lore = new ArrayList<>();
					if (!poss.get(0).equals(TownManager.getInstance().getTown(getTown()).getMayor()))
						lore.add(ChatColor.RED + "[Click to Kick]");
					else
						lore.add(ChatColor.GREEN + "[Town Mayor]");
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

			if (i == 0)
				members = inv;
		}
	}

	public void openMenu(Player p) {
		p.openInventory(getMenu());
	}

	public void openInfoMenu(Player p) {
		setupInfoMenu();
		p.openInventory(getInfoMenu());
	}

	public Inventory openPlotFlagInventory(Plot plot) {

		Inventory inv = getPlotFlagInventories().containsKey(plot) ? getPlotFlagInventories().get(plot) : Bukkit.createInventory(new TownInventoryHolder(true, getTown()), 27, ChatColor.BLUE + "" + ChatColor.BOLD + "Flags for (" + plot.getX() + ", " + plot.getZ() + ") in " + plot.getWorldName());

		for (int i = 0; i < TownFlag.values().length; i++) {
			TownFlag flag = TownFlag.values()[i];
			int status = plot.hasFlag(flag) ? plot.getFlag(flag) ? 1 : 0 : 2;

			ItemStack item = new ItemStack(status == 0 ? Material.RED_BANNER : status == 1 ? Material.GREEN_BANNER : Material.GRAY_BANNER);
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				meta.setDisplayName((status == 1 ? ChatColor.GREEN : status == 0 ? ChatColor.RED : ChatColor.GRAY) + flag.getName());
				List<String> lore = new ArrayList<>();
				lore.add("" + ChatColor.YELLOW + ChatColor.ITALIC + flag.getDescription());
				boolean inheritedValue = TownManager.getInstance().getTown(town).getFlag(flag);
				lore.add(ChatColor.WHITE + "Status: " + (status == 1 ? ChatColor.GREEN + "Enabled" : status == 0 ? ChatColor.RED + "Disabled" : ChatColor.GRAY + "Inherits Town Flag (" + (inheritedValue ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.GRAY + ")"));
				meta.setLore(lore);
			}
			item.setItemMeta(meta);

			inv.setItem(i, item);
		}

		inv.setItem(22, close);

		getPlotFlagInventories().put(plot, inv);

		return inv;
	}

	public void closePlotFlagInventory(Plot p) {
		getPlotFlagInventories().remove(p);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (getPlotFlagInventories().values().contains(e.getInventory())) {
			for (Plot p : new ArrayList<>(getPlotFlagInventories().keySet())) {
				if (getPlotFlagInventories().get(p).equals(e.getInventory())) {
					closePlotFlagInventory(p);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {

		TownInventoryHolder holder = null;
		if (e.getInventory().getHolder() instanceof TownInventoryHolder)
			holder = (TownInventoryHolder) e.getInventory().getHolder();

		if (holder != null)
			if (!holder.getOwner().equalsIgnoreCase(getTown()))
				return;

		int slot = e.getRawSlot();

		if (slot >= e.getInventory().getSize())
			return;


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
			} else if (e.getInventory().equals(getFlags())) {
				openMenu(p);
				return;
			} else if (e.getInventory().getHolder() instanceof TownInventoryHolder) {
				if (holder.getMenuGroup() == TownMenuGroup.MEMBER_LIST) {
					openInfoMenu(p);
					return;
				} else if (holder.getMenuGroup() == TownMenuGroup.INVITE) {
					openMenu(p);
					return;
				}

			}
		}

		//is menu
		if (e.getInventory().equals(getMenu())) {
			if (e.getRawSlot() == 10) {
				openInfoMenu((Player) e.getWhoClicked());
			} else if (e.getRawSlot() == 12) {
				if (TheNewTown.getInstance().isWarEnabled()) {
					Town town = TownManager.getInstance().getTown(getTown());
					p.openInventory(town.getWarInventory().getMenu());
				} else {
					p.sendMessage(ChatColor.RED + "War is not allowed on this server!");
				}
			} else if (e.getRawSlot() == 14) {
				setupInviteMenu();
				if (invite != null)
					e.getWhoClicked().openInventory(invite);
			} else if (e.getRawSlot() == 16) {
				setupFlagsMenu();
				p.openInventory(getFlags());
			}

		}

		//is info menu
		if (e.getInventory().equals(getInfoMenu())) {
			if (e.getRawSlot() == 15) {
				setupMembersMenu();
				if (members != null)
					p.openInventory(members);
			}
		}

		//is invite menu
		if (holder != null)
			if (holder.getMenuGroup() == TownMenuGroup.INVITE) {
				if (e.getCurrentItem() == null)
					return;

				ItemStack item = e.getCurrentItem();
				if (item.equals(prev)) {
					if (holder.getPrevPage() != null)
						p.openInventory(holder.getPrevPage());
				} else if (item.equals(next)) {
					if (holder.getNextPage() != null)
						p.openInventory(holder.getNextPage());
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
		if (holder != null)
			if (holder.getMenuGroup() == TownMenuGroup.MEMBER_LIST) {
				ItemStack item = e.getCurrentItem();
				if (item.equals(prev)) {
					if (holder.getPrevPage() != null)
						p.openInventory(holder.getPrevPage());
				} else if (item.equals(next)) {
					if (holder.getNextPage() != null)
						p.openInventory(holder.getNextPage());
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
							p.openInventory(getInfoMenu());
						} else
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
					} else {
						p.sendMessage(ChatColor.RED + "You must be the mayor of a town to kick players!");
					}
				}
			}

		//is flags menu
		if (e.getInventory().equals(getFlags())) {
			TownFlag flag;

			if (slot >= TownFlag.values().length)
				return;

			flag = TownFlag.values()[slot];

			Town town = TownManager.getInstance().getTown(getTown());
			town.setFlag(flag, !town.getFlag(flag));
			town.save();
			setupFlagsMenu();
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 1.0f, 0.4f);
		}

		if (getPlotFlagInventories().values().contains(e.getInventory())) {
			TownFlag flag;

			if (slot >= TownFlag.values().length)
				return;

			flag = TownFlag.values()[slot];

			Plot plot = TownManager.getInstance().getTown(getTown()).getPlot(p.getLocation().getChunk());

			if (plot == null) {
				p.closeInventory();
				p.sendMessage(ChatColor.RED + "That wasn't supposed to happen...");
				return;
			}

			if (plot.hasFlag(flag)) {
				if (plot.getFlag(flag))
					plot.setFlag(flag, false);
				else
					plot.removeFlag(flag);
			} else
				plot.setFlag(flag, true);

			openPlotFlagInventory(plot);
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 1.0f, 0.4f);
		}
	}

}
