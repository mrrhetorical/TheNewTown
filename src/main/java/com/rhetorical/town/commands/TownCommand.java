package com.rhetorical.town.commands;

import com.rhetorical.town.TheNewTown;
import com.rhetorical.town.towns.*;
import com.rhetorical.town.towns.flags.TownFlag;
import com.rhetorical.town.towns.invite.InviteManager;
import com.rhetorical.town.util.Position;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TownCommand {

	enum CommandData {

		Help(ChatColor.YELLOW + "/t help {page}" + ChatColor.RED + " - " + ChatColor.WHITE + "Shows the help messages for the given page, (or first page if none given).", "tnt.help"), // done
		Open(ChatColor.YELLOW + "/t [name]" + ChatColor.RED + " - " + ChatColor.WHITE + "Opens the gui for the town with the given name. (m)", "tnt.gui"),
		Create(ChatColor.YELLOW + "/t create [name]" + ChatColor.RED + " - " + ChatColor.WHITE + "Claims the plot you're standing in and creates a new town with the given name.", "tnt.create"), // done
		Delete(ChatColor.YELLOW + "/t delete [name]" + ChatColor.RED + " - " + ChatColor.WHITE + "Deletes the town as mayor. (m)", "tnt.delete"), // done
		Join(ChatColor.YELLOW + "/t join [town name]" + ChatColor.RED + " - " + ChatColor.WHITE + "Attempts to join the town with the given name.", "tnt.join"), // done
		Invite(ChatColor.YELLOW + "/t invite [player]" + ChatColor.RED + " - " + ChatColor.WHITE + "Invites the given player to your town. (m)", "tnt.invite"), // done
		Claim(ChatColor.YELLOW + "/t claim" + ChatColor.RED + " - " + ChatColor.WHITE + "Claims the plot you're standing in for your town. (m)", "tnt.claim"), // done
		Unclaim(ChatColor.YELLOW + "/t unclaim" + ChatColor.RED + " - " + ChatColor.WHITE + "Unclaims the plot you're standing in from your town. (m)", "tnt.unclaim"), // done
		Sell(ChatColor.YELLOW + "/t sell [cost]" + ChatColor.RED + " - " + ChatColor.WHITE + "Sells the current plot for the given price. A cost of -1 removes from market. (m)", "tnt.sell"), // done
		Buy(ChatColor.YELLOW + "/t lease (release)" + ChatColor.RED + " - " + ChatColor.WHITE + "Leases the current plot from the town or releases lease.", "tnt.lease"), // done
		Deposit(ChatColor.YELLOW + "/t deposit [amount]" + ChatColor.RED + " - " + ChatColor.WHITE + "Deposits money to the town bank.", "tnt.deposit"),
		Withdraw(ChatColor.YELLOW + "/t withdraw [amount]" + ChatColor.RED + " - " + ChatColor.WHITE + "Withdraws money from the town bank.", "tnt.withdraw"),
		Balance(ChatColor.YELLOW + "/t balance" + ChatColor.RED + " - " + ChatColor.WHITE + "Checks the balance of your town.", "tnt.balance"),
		Flag(ChatColor.YELLOW + "/t flag [plot/town] [flag] [true/false/clear]" + ChatColor.RED + " - " + ChatColor.WHITE + "Sets the flag for the given plot or town. (m)", "tnt.flag"), // done
		Flags(ChatColor.YELLOW + "/t flags" + ChatColor.RED + " - " + ChatColor.WHITE + "Attempts to open the flag override gui for the current plot.", "tnt.flag"),
		Tax(ChatColor.YELLOW + "/t tax [value]" + ChatColor.RED + " - " + ChatColor.WHITE + "Sets the tax rate for your town. (m)", "tnt.tax"), // done
		Info(ChatColor.YELLOW + "/t info [town]" + ChatColor.RED + " - " + ChatColor.WHITE + "Shows you info about the given town.", "tnt.info"), // done
		Here(ChatColor.YELLOW + "/t here" + ChatColor.RED + " - " + ChatColor.WHITE + "Checks current plot to see who it belongs to.", "tnt.here"), // done
		List(ChatColor.YELLOW + "/t list [page]" + ChatColor.RED + " - " + ChatColor.WHITE + "Lists all the towns, their type, their mayor, and they population.", "tnt.list"),
		Leave(ChatColor.YELLOW + "/t leave" + ChatColor.RED + " - " + ChatColor.WHITE + "Leaves your town.", "tnt.leave"), // done
		SetHome(ChatColor.YELLOW + "/t sethome" + ChatColor.RED + " - " + ChatColor.WHITE + "Sets the home for the town. (m)", "tnt.home"), // done
		Home(ChatColor.YELLOW + "/t home" + ChatColor.RED + " - " + ChatColor.WHITE + "Teleports you to the home for the town.", "tnt.home"), // done
		Kick(ChatColor.YELLOW + "/t kick [name]" + ChatColor.RED + " - " + ChatColor.WHITE + "Kicks a player from your town. (m)", "tnt.kick"),
		MAP(ChatColor.YELLOW + "/t map (auto)" + ChatColor.RED + " - " + ChatColor.WHITE + "Shows a map of the surrounding chunks. 'Auto' automatically updates the map.", "tnt.map"),
		Border(ChatColor.YELLOW + "/t border (auto)" + ChatColor.RED + " - " + ChatColor.WHITE + "Shows a border of your town. 'Auto' automatically updates the border with changes in height.", "tnt.map"); // done

		private String message,
				permission;

		CommandData(String m, String p) {
			message = m;
			permission = p;
		}

		String getMessage() {
			return message;
		}

		String getPermission() {
			return permission;
		}
	}

	private String pageHeader = ChatColor.RED + "#####  [ " + ChatColor.WHITE + "TheNewTown Help (" + ChatColor.YELLOW + "%s" + ChatColor.WHITE + "/" + ChatColor.YELLOW + "%s" + ChatColor.WHITE + ") " + ChatColor.RED + "] #####";

	private final int messagesPerPage = 5;

	private TownCommand() {}

	public static TownCommand getInstance() {
		return new TownCommand();
	}

	@SuppressWarnings("Duplicates")
	public void onCommand(CommandSender sender, Command cmd, String label, String... args) {

		if (args.length == 0) {
			args = new String[]{"help"};
		}

		if (args[0].equalsIgnoreCase("help")) {
			if (!checkPerm(sender, CommandData.Help))
				return;

			int page = 1;
			if (args.length == 2) {
				try {
					page = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + "Invalid page number!");
					return;
				}
			}

			CommandData[] messages = CommandData.values();

			int maxMessages = 0;
			for (int i = 0; i < messages.length; i++) {
				if (hasPermission(sender, messages[i].getPermission()))
					maxMessages++;
			}

			int pages = maxMessages % 5 == 0 ? maxMessages / 5 : maxMessages / 5 + 1;
			int roof = (page - 1) * messagesPerPage + 5;

			if (page > pages) {
				sender.sendMessage(ChatColor.RED + "No such page exists!");
				return;
			}

			sender.sendMessage(String.format(pageHeader, page, pages));

			for (int i = (page - 1) * messagesPerPage + 1; i < roof && i < CommandData.values().length; i++) {
				if (hasPermission(sender, messages[i].getPermission()))
					sender.sendMessage(messages[i].getMessage());
				else
					roof++;
			}

		}
		else if (args[0].equalsIgnoreCase("create")) {
			if (!checkPerm(sender, CommandData.Create))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Improper usage! Correct usage: /t create [name]");
				return;
			}

			String name = args[1];

			if (TownManager.getInstance().getTown(name) != null) {
				sender.sendMessage(ChatColor.RED + "Could not create town! A town already exists with that name!");
				return;
			}

			Player p = (Player) sender;
			Chunk chunk = p.getLocation().getChunk();
			if (TownManager.getInstance().isChunkClaimed(chunk)) {
				sender.sendMessage(ChatColor.RED + "Could not create town! A town already exists at your current location!");
				return;
			}

			if (!TownManager.getInstance().createTown(p.getUniqueId(), chunk, name)) {
				sender.sendMessage(ChatColor.RED + "Could not create town!");
				return;
			}

			sender.sendMessage(ChatColor.GREEN + "Successfully created town " + ChatColor.GOLD + name + ChatColor.GREEN + "!");
		}
		else if (args[0].equalsIgnoreCase("delete")) {
			if (!checkPerm(sender, CommandData.Delete))
				return;

			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Improper usage! Correct usage: /t delete [name]");
				return;
			}

			if (sender.isOp() || sender.hasPermission("tnt.admin") || sender instanceof ConsoleCommandSender) {
				String town = args[1];
				Town t = TownManager.getInstance().getTown(town);
				if (t == null) {
					sender.sendMessage(ChatColor.RED + "No such town exists with that name!");
					return;
				}

				TownManager.getInstance().deleteTown(town);
				sender.sendMessage(ChatColor.GREEN + "Successfully deleted town!");
				return;
			}

			if (!(sender instanceof Player))
				return;

			Player p = (Player) sender;

			Town t = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());
			if(!t.getMayor().equals(p.getUniqueId())) {
				sender.sendMessage(ChatColor.RED + "You can only delete towns as a town Mayor!");
				return;
			}

			String town = args[1];

			if (!town.equals(t.getName())) {
				sender.sendMessage(ChatColor.RED + "Could not delete town! You can only delete your own town!");
				return;
			}

			TownManager.getInstance().deleteTown(town);
			sender.sendMessage(ChatColor.GREEN + "Successfully deleted town!");
			return;
		}
		else if (args[0].equalsIgnoreCase("claim")) {
			if (!checkPerm(sender, CommandData.Claim))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			Player p = (Player) sender;

			Town town = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());

			if (town == null) {
				p.sendMessage(ChatColor.RED + "You do not belong to a town! Ask for an invite to a town, or create one using '/t create [name]'!");
				return;
			}

			if (!town.getMayor().equals(p.getUniqueId())) {
				p.sendMessage(ChatColor.RED + "You must be a mayor to do that!");
				return;
			}

			Chunk chunk = p.getLocation().getChunk();

			if (TownManager.getInstance().isChunkClaimed(chunk)) {
				p.sendMessage(ChatColor.RED + "That chunk is already claimed!");
				return;
			}

			EconomyResponse response = TheNewTown.getInstance().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), town.getTownType().getClaimCost());
			if (!response.transactionSuccess()) {
				p.sendMessage(ChatColor.RED + String.format("You could not afford the $%s it costs to claim that plot!", town.getTownType().getClaimCost()));
				return;
			}

			if (town.addPlot(chunk)) {
				p.sendMessage(ChatColor.GREEN + String.format("Successfully claimed plot for your town! $%s has been withdrawn from your account!", town.getTownType().getClaimCost()));
				town.save();
			} else {
				TheNewTown.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), town.getTownType().getClaimCost());
				p.sendMessage(ChatColor.RED + String.format("Could not claim plot! $%s has been refunded to your account!", town.getTownType().getClaimCost()));
			}

			return;
		}
		else if (args[0].equalsIgnoreCase("unclaim")) {
			if (!checkPerm(sender, CommandData.Unclaim))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			Player p = (Player) sender;

			Town town = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());

			if (town == null) {
				p.sendMessage(ChatColor.RED + "You do not belong to a town! Ask for an invite to a town, or create one using '/t create [name]'!");
				return;
			}

			if (!town.getMayor().equals(p.getUniqueId())) {
				p.sendMessage(ChatColor.RED + "You must be a mayor to do that!");
				return;
			}


			Chunk chunk = p.getLocation().getChunk();

			if (!town.isChunkClaimed(chunk)) {
				p.sendMessage(ChatColor.RED + "That chunk is not claimed by your town!");
				return;
			}


			if (town.removePlot(chunk)) {
				p.sendMessage(ChatColor.GREEN + "Successfully unclaimed plot for your town!");
			} else {
				p.sendMessage(ChatColor.RED + "Could not unclaim plot!");
			}

			return;
		}
		else if (args[0].equalsIgnoreCase("here")) {
			if (!checkPerm(sender, CommandData.Here))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			Player p = (Player) sender;

			Town town = TownManager.getInstance().getTown(p.getLocation().getChunk());
			if (town == null) {
				p.sendMessage(ChatColor.GRAY + "The current chunk is unclaimed.");
				return;
			} else {
				p.sendMessage(ChatColor.GRAY + "The current chunk is claimed by " + ChatColor.GOLD + town.getName() + ChatColor.GRAY + ".");
				return;
			}
		}
		else if (args[0].equalsIgnoreCase("invite")) {
			if (!checkPerm(sender, CommandData.Invite))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /t invite [player]");
				return;
			}

			Player p = (Player) sender;

			Town t = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());

			if (t == null) {
				p.sendMessage(ChatColor.RED + "You do not belong to a town! Ask for an invite to a town, or create one using '/t create [name]'!");
				return;
			}

			if (!t.getMayor().equals(p.getUniqueId())) {
				p.sendMessage(ChatColor.RED + "You must be a mayor to invite people to your town!");
				return;
			}

			String pName = args[1];
			Player target = Bukkit.getPlayer(pName);

			if (target == null || !target.isOnline()) {
				p.sendMessage(ChatColor.RED + "Could not invite player to your town!");
				return;
			}

			if (target.equals(p)) {
				p.sendMessage(ChatColor.RED + "You can't invite yourself to your own town!");
				return;
			}

			if (!InviteManager.getInstance().generateRequest(p.getUniqueId(), target.getUniqueId(), t.getName())) {
				p.sendMessage(ChatColor.RED + "That player is already in the town!");
			}
			return;
		}
		else if (args[0].equalsIgnoreCase("join")) {
			if (!checkPerm(sender, CommandData.Join))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			Player p = (Player) sender;

			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /t join [name]");
				return;
			}

			String townName = args[1];
			Town town = TownManager.getInstance().getTown(townName);
			if (town == null) {
				sender.sendMessage(ChatColor.RED + "No such town exists with that name!");
				return;
			}

			if (town.getResidents().contains(p.getUniqueId())) {
				sender.sendMessage(ChatColor.RED + "You already are in that town!");
				return;
			}

			if (!InviteManager.getInstance().tryAcceptRequest(p.getUniqueId(), town.getName())) {
				sender.sendMessage(ChatColor.RED + "Could not join that town! You require an invite to join towns!");
				return;
			} else {
				sender.sendMessage(ChatColor.GREEN + String.format("Successfully joined the town %s!", town.getName()));
				town.save();
				return;
			}

		}
		else if (args[0].equalsIgnoreCase("leave")) {
			if (!checkPerm(sender, CommandData.Leave))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			Player p = (Player) sender;

			Town t = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());
			if (t == null) {
				p.sendMessage(ChatColor.RED + "You can't leave your current town because you don't belong to one!");
				return;
			}

			if (t.getMayor().equals(p.getUniqueId())) {
				p.sendMessage(ChatColor.RED + "You may not abandon your people! You may instead delete the town with the '/t delete (...)' command!");
				return;
			}

			if (t.removePlayer(p.getUniqueId())) {
				p.sendMessage(ChatColor.GREEN + String.format("Successfully left town %s!", t.getName()));
				t.save();
				return;
			} else {
				p.sendMessage(ChatColor.RED + "Could not leave town!");
				return;
			}
		}
		else if (args[0].equalsIgnoreCase("kick")) {
			if (!checkPerm(sender, CommandData.Kick))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			Player p = (Player) sender;

			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /t kick [player]");
				return;
			}

			Town t = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());
			if (t == null) {
				p.sendMessage(ChatColor.RED + "You don't belong to a town!");
				return;
			}

			if (!t.getMayor().equals(p.getUniqueId())) {
				p.sendMessage(ChatColor.RED + "You must be a mayor to kick players out of your town!");
				return;
			}

			String pName = args[1];
			OfflinePlayer target = Bukkit.getOfflinePlayer(pName);

			if (p.getUniqueId().equals(target.getUniqueId())) {
				p.sendMessage(ChatColor.RED + "You cannot kick yourself from your town!");
				return;
			}

			if (!t.getResidents().contains(target.getUniqueId())) {
				p.sendMessage(ChatColor.RED + "That player is not a resident of your town!");
				return;
			}

			if (t.getMayor().equals(target.getUniqueId())) {
				p.sendMessage(ChatColor.RED + "Cannot kick the mayor of the town!");
				return;
			}

			if (t.removePlayer(target.getUniqueId())) {
				p.sendMessage(ChatColor.GREEN + "Successfully kicked player from your town!");
				t.save();
				return;
			} else {
				p.sendMessage(ChatColor.RED + "Could not kick player from your town!");
				return;
			}
		}
		else if (args[0].equalsIgnoreCase("info")) {
			if (!checkPerm(sender, CommandData.Info))
				return;

			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /t info [name]");
				return;
			}

			String tName = args[1];
			Town town = TownManager.getInstance().getTown(tName);
			if (town == null) {
				sender.sendMessage(ChatColor.RED + "No such town exists with that name!");
				return;
			}
			sender.sendMessage("##### [Town Info] #####");
			sender.sendMessage(String.format("Town: %s", town.getName()));
			sender.sendMessage(String.format("Mayor: %s", Bukkit.getOfflinePlayer(town.getMayor()).getName()));
			sender.sendMessage(String.format("Wealth: %s", town.getBank()));
			sender.sendMessage(String.format("Size: %s (%s)", town.getTownType().getReadable(), town.getPlots().size()));
			sender.sendMessage(String.format("Residents: %s", town.getResidents().size()));
			float dailyTax = town.getTax() * (24f / (float) TownManager.getInstance().getTaxPeriod());
			dailyTax = ((int) ((dailyTax + 0.005f) * 100)) / 100f;
			sender.sendMessage(String.format("Taxes: $%s/day", dailyTax));
			float upkeep = TownManager.getInstance().getUpkeep(town) * (24f / (float) TownManager.getInstance().getUpkeepPeriod());
			upkeep = ((int) ((upkeep + 0.005f) * 100)) / 100f;
			sender.sendMessage(String.format("Upkeep: $%s/day", upkeep));
			return;
		}
		else if (args[0].equalsIgnoreCase("tax")) {
			if (!checkPerm(sender, CommandData.Tax))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /t tax [amount]");
				return;
			}

			Player p = (Player) sender;

			Town town = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());
			if (town == null) {
				p.sendMessage(ChatColor.RED + "You can't do that because you don't belong to a town!");
				return;
			}

			if (!town.getMayor().equals(p.getUniqueId())) {
				p.sendMessage(ChatColor.RED + "You must be a mayor to set the tax rate!");
				return;
			}

			float amount;
			String a = args[1];

			try {
				amount = Float.parseFloat(a);
			} catch (Exception e) {
				p.sendMessage(ChatColor.RED + "Invalid tax amount!");
				return;
			}

			if (amount < 0f) {
				p.sendMessage(ChatColor.RED + "You can't have a negative tax rate!");
				return;
			} else if (amount > TownManager.getInstance().getUpkeep(town)) {
				p.sendMessage(ChatColor.RED + "You can't set a tax rate that's higher than your upkeep!");
				return;
			}

			town.setTax(amount);
			town.save();
			p.sendMessage(ChatColor.GREEN + "Successfully changed your town's tax rate!");
			return;
		}
		else if (args[0].equalsIgnoreCase("sell")) {
			if (!checkPerm(sender, CommandData.Sell))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /t sell [cost/-1]");
				return;
			}

			Player p = (Player) sender;

			Town town = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());

			if (town == null || !town.isChunkClaimed(p.getLocation().getChunk())) {
				p.sendMessage(ChatColor.RED + "You can't sell a plot you don't own!");
				return;
			}

			if (!town.getMayor().equals(p.getUniqueId())) {
				p.sendMessage(ChatColor.RED + "You must be a mayor to sell plots!");
				return;
			}

			Plot plot = town.getPlot(p.getLocation().getChunk());

			float cost;

			try {
				cost = Float.parseFloat(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Invalid cost!");
				return;
			}

			if (cost < 0f && cost != -1) {
				p.sendMessage(ChatColor.RED + "Cost cannot be below 0!");
				return;
			}


			if (plot.getLeaser() != null) {
				if (cost != -1f) {
					p.sendMessage(ChatColor.RED + "Cannot change price of plot while player is leasing!");
					return;
				} else {
					plot.setForSale(false);
					plot.setCost(0f);
					town.save();
					p.sendMessage(ChatColor.YELLOW + "Plot has been removed from sale. The player currently leasing will have access until the end of the tax period.");
					return;
				}
			} else {
				if (cost == -1) {
					plot.setForSale(false);
					plot.setCost(0f);
					town.save();
					p.sendMessage(ChatColor.GREEN + "Plot has been removed from sale.");
					return;
				} else {
					plot.setForSale(true);
					plot.setCost(cost);
					town.save();
					p.sendMessage(ChatColor.GREEN + String.format("This plot is now going for sale at $%s!", plot.getCost()));
					return;
				}
			}
		}
		else if (args[0].equalsIgnoreCase("lease")) {
			if (!checkPerm(sender, CommandData.Buy))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			Player p = (Player) sender;

			Town town = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());
			if (town == null) {
				p.sendMessage(ChatColor.RED + "You must belong to a town to lease plots!");
				return;
			}

			if (town.getMayor().equals(p.getUniqueId())) {
				p.sendMessage(ChatColor.RED + "You cannot lease plots as the town mayor!");
				return;
			}

			if (!town.isChunkClaimed(p.getLocation().getChunk())) {
				p.sendMessage(ChatColor.RED + "Your town does not own this plot!");
				return;
			}

			Plot plot = town.getPlot(p.getLocation().getChunk());

			if (plot.getLeaser() != null) {
				if (plot.isForSale()) {
					if (plot.getLeaser().equals(p.getUniqueId())) {

						if (args.length != 2) {
							p.sendMessage(ChatColor.RED + "You already are leasing this plot!");
							return;
						}

						if (!args[1].equalsIgnoreCase("release")) {
							p.sendMessage(ChatColor.RED + "Improper usage! Proper usage: /t lease release");
							return;
						}

						plot.setLeaser(null);
						town.save();
						EconomyResponse response = TheNewTown.getInstance().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), plot.getCost());
						if (response.transactionSuccess()) {
							p.sendMessage(ChatColor.YELLOW + String.format("Successfully paid remaining $%s due!", plot.getCost()));
							TheNewTown.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(town.getMayor()), plot.getCost());
						} else
							p.sendMessage(ChatColor.RED + String.format("Could not pay remaining $%s due!", plot.getCost()));

						p.sendMessage(ChatColor.GREEN + "Successfully released lease agreement!");
						return;
					} else {
						p.sendMessage(ChatColor.RED + "Someone else is already leasing this plot!");
						return;
					}
				} else {
					if (!plot.getLeaser().equals(p.getUniqueId()))
						p.sendMessage(ChatColor.RED + "This plot is not for sale!");
					else {
						if (!args[1].equalsIgnoreCase("release")) {
							p.sendMessage(ChatColor.RED + "Improper usage! Proper usage: /t lease release");
							return;
						}

						plot.setLeaser(null);
						town.save();
						p.sendMessage(ChatColor.GREEN + "Successfully released lease agreement!");
						return;
					}
				}
			} else {
				if (!plot.isForSale()) {
					p.sendMessage(ChatColor.RED + "This plot is not for sale!");
					return;
				}

				EconomyResponse response = TheNewTown.getInstance().getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), plot.getCost());
				if (!response.transactionSuccess()) {
					p.sendMessage(ChatColor.RED + String.format("Could not pay $%s required to lease plot!", plot.getCost()));
					return;
				}

				TheNewTown.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(plot.getOwner()), plot.getCost());

				plot.setLeaser(p.getUniqueId());
				town.save();

				p.sendMessage(ChatColor.GREEN + "Successfully began lease agreement!");
				return;
			}
		}
		else if (args[0].equalsIgnoreCase("flag")) {
			if (!checkPerm(sender, CommandData.Flag))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			Player p = (Player) sender;

			Town town = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());
			if (town == null) {
				p.sendMessage(ChatColor.RED + "You must belong to a town to set it's flags!");
				return;
			}

			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /t flag [plot/town] [flag] [true/false/clear]");
				return;
			}


			boolean isTown;

			if (args[1].equalsIgnoreCase("plot"))
				isTown = false;
			else if (args[1].equalsIgnoreCase("town"))
				isTown = true;
			else {
				p.sendMessage(ChatColor.RED + "Please specify if the flag should be for the town or the plot!");
				return;
			}

			if (args.length == 2) {
				p.sendMessage(ChatColor.RED + "Please enter a valid flag! Valid flags are no_pvp, allow_modification, allow_pickup, allow_drop, mob_spawning, fire_tick, lava_flow, water_flow, animal_abuse, and alien_interact.");
				return;
			}

			TownFlag flag;

			try {
				flag = TownFlag.valueOf(args[2].toUpperCase());
			} catch (Exception e) {
				p.sendMessage(ChatColor.RED + "Please enter a valid flag! Valid flags are no_pvp, allow_modification, allow_pickup, allow_drop, mob_spawning, fire_tick, lava_flow, water_flow, animal_abuse, and alien_interact.");
				return;
			}

			if (args.length != 4) {
				sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /t flag [plot/town] [flag] [true/false/clear]");
				return;
			}


			if (!isTown) {
				if (!town.isChunkClaimed(p.getLocation().getChunk())) {
					p.sendMessage(ChatColor.RED + "You can't set flags for a plot your town does not own!");
					return;
				}

				Plot plot = town.getPlot(p.getLocation().getChunk());

				if (!p.getUniqueId().equals(plot.getLeaser()) && !town.getMayor().equals(p.getUniqueId())) {
					p.sendMessage(ChatColor.RED + "You must be the town mayor or leasing the plot to change the flags of it!");
					return;
				}

				if (args[3].equalsIgnoreCase("clear")) {
					plot.removeFlag(flag);
					town.save();
					p.sendMessage(ChatColor.GREEN + "Successfully cleared flag from plot!");
					return;
				} else {
					boolean value;
					try {
						value = Boolean.parseBoolean(args[3]);
					} catch (Exception e) {
						p.sendMessage(ChatColor.RED + "For the flag value, please enter either 'true', 'false', or 'clear'!");
						return;
					}

					plot.setFlag(flag, value);
					town.save();
					p.sendMessage(ChatColor.GREEN + "Successfully set flag for plot!");
					return;
				}
			} else {
				if (!town.getMayor().equals(p.getUniqueId())) {
					p.sendMessage(ChatColor.RED + "You must be the town mayor flags of the town!");
					return;
				}

				boolean value;
				try {
					value = Boolean.parseBoolean(args[3]);
				} catch (Exception e) {
					p.sendMessage(ChatColor.RED + "For the flag value, please enter either 'true' or 'false'!");
					return;
				}

				town.setFlag(flag, value);
				town.save();
				p.sendMessage(ChatColor.GREEN + "Successfully set flag for town!");
				return;
			}
		}
		else if (args[0].equalsIgnoreCase("flags")) {
			if (!checkPerm(sender, CommandData.Flags))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			Player p = (Player) sender;

			Town t = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());

			if (t == null) {
				p.sendMessage(ChatColor.RED + "You must belong to a town to set it's flags!");
				return;
			}

			Plot plot = t.getPlot(p.getLocation().getChunk());
			if (plot == null) {
				p.sendMessage(ChatColor.RED + "That plot does not belong to your town!");
				return;
			}

			if ((plot.getLeaser() != null && !plot.getLeaser().equals(p.getUniqueId())) && !t.getMayor().equals(p.getUniqueId())) {
				p.sendMessage(ChatColor.RED + "You don't have permission to change this plot's flags!");
				return;
			}

			p.openInventory(t.getInventory().openPlotFlagInventory(plot));

			return;
		}
		else if (args[0].equalsIgnoreCase("list")) {
			if (!checkPerm(sender, CommandData.List))
				return;

			if (args.length == 1)
				args = new String[]{"list", "1"};

			int page;

			try {
				page = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Invalid page number!");
				return;
			}

			int perPage = 5;

			List<Town> townMap = TownManager.getInstance().getOrderedTowns();

			int lastPage = (int) Math.ceil((double) townMap.size() / (double) perPage);

			if (lastPage != 0 && (page > lastPage || page <= 0)) {
				sender.sendMessage(ChatColor.RED + "No such page exists!");
				return;
			}

			int offset = (page - 1) * perPage;

			sender.sendMessage(String.format("##### [Towns List %s/%s] #####", page, (lastPage != 0 ? lastPage : 1)));

			if (lastPage != 0)
				for (int i = (townMap.size() - 1) - offset; i >= 0 && i > (townMap.size() - 1) - offset - perPage; i--)
					try {
						sender.sendMessage(getTownShortListing(townMap.get(i), townMap.size() - i));
					} catch (Exception|Error ignored) {
						Bukkit.getLogger().info(String.format("Error in showing town list! (Page, Entry): (%s, %s)", page, i));
					}

			return;
		}
		else if (args[0].equalsIgnoreCase("setHome")) {
			if (!checkPerm(sender, CommandData.SetHome))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			Player p = (Player) sender;

			Town town = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());

			if (town == null) {
				p.sendMessage(ChatColor.RED + "You must belong to a town to use this command!");
				return;
			}

			if (!town.getMayor().equals(p.getUniqueId())) {
				p.sendMessage(ChatColor.RED + "You must be the town mayor to set the town's home!");
				return;
			}

			if (!town.isChunkClaimed(p.getLocation().getChunk())) {
				p.sendMessage(ChatColor.RED + "You can only set the town's home within a plot you own!");
				return;
			}

			Position pos = Position.fromLocation(p.getLocation());
			town.setHome(pos);
			town.save();
			p.sendMessage(ChatColor.GREEN + "Successfully set town's home!");
			return;
		}
		else if (args[0].equalsIgnoreCase("home")) {
			if (!checkPerm(sender, CommandData.Home))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			Player p = (Player) sender;

			Town town = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());

			if (town == null) {
				p.sendMessage(ChatColor.RED + "You must belong to a town to use this command!");
				return;
			}

			if (town.getHome() == null) {
				p.sendMessage(ChatColor.RED + "Your town does not have a home set!");
				return;
			}

			Location to = town.getHome().toLocation();
			if (to != null) {
				p.teleport(to);
				p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.2f, 1f);
			}
			return;
		}
		else if (args[0].equalsIgnoreCase("map")) {
			if (!checkPerm(sender, CommandData.MAP))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			if (args.length == 1) {
				MapManager.getInstance().showMap((Player) sender);
				return;
			} else if (!args[1].equalsIgnoreCase("auto") || args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /t map (auto)");
				return;
			} else {
				MapManager.getInstance().autoShowMap((Player) sender);
				sender.sendMessage(ChatColor.GREEN + String.format("Auto show map is now %s!", MapManager.getInstance().isAutoShowMap((Player) sender) ? "enabled" : "disabled"));
				return;
			}



		} else if (args[0].equalsIgnoreCase("border")) {
			if (!checkPerm(sender, CommandData.Border))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			Player p = (Player) sender;

			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("auto")) {
					BorderManager.getInstance().autoShowBorder(p);
					sender.sendMessage(ChatColor.GREEN + String.format("Auto show borders is now %s!", BorderManager.getInstance().isAutoShowBorder(p) ? "enabled" : "disabled"));
					return;
				}

				sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /t border (auto)");
				return;
			}

			BorderManager.getInstance().showBorder(p);
		} else if (args[0].equalsIgnoreCase("balance")) {
			if (!checkPerm(sender, CommandData.Balance))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			Player p = (Player) sender;

			Town town = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());

			if (town == null) {
				p.sendMessage(ChatColor.RED + "You must belong to a town to use this command!");
				return;
			}

			sender.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + town.getName() + "'s balance: " + ChatColor.GREEN + "$" + town.getBank());

		} else if (args[0].equalsIgnoreCase("deposit")) {
			if (!checkPerm(sender, CommandData.Balance))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			Player p = (Player) sender;

			Town town = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());

			if (town == null) {
				p.sendMessage(ChatColor.RED + "You must belong to a town to use this command!");
				return;
			}

			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /t deposit [amount]");
				return;
			}

			float amount;

			try {
				amount = Float.parseFloat(args[1]);
			} catch (Error|Exception e) {
				sender.sendMessage(ChatColor.RED + "Amount to deposit must be a number!");
				return;
			}

			if (!town.depositToBank(p.getUniqueId(), amount))
				sender.sendMessage(ChatColor.RED + String.format("Could not deposit $%s to your town's bank!", amount));
			else {
				town.setBank(town.getBank() + amount);
				town.save();
				sender.sendMessage(ChatColor.GREEN + String.format("Successfully deposited $%s into your town's bank! New balance: $%s", amount, town.getBank()));
			}
		} else if (args[0].equalsIgnoreCase("withdraw")) {
			if (!checkPerm(sender, CommandData.Balance))
				return;

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return;
			}

			Player p = (Player) sender;

			Town town = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());

			if (town == null) {
				p.sendMessage(ChatColor.RED + "You must belong to a town to use this command!");
				return;
			}

			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Incorrect usage! Correct usage: /t deposit [amount]");
				return;
			}

			float amount;

			try {
				amount = Float.parseFloat(args[1]);
			} catch (Error|Exception e) {
				sender.sendMessage(ChatColor.RED + "Amount to withdraw must be a number!");
				return;
			}

			if (town.getBank() - amount < 0f) {
				sender.sendMessage(ChatColor.RED + "Your town's bank does not have the funds for you to withdraw that amount!");
				return;
			}

			if (!town.withdrawFromBank(p.getUniqueId(), amount))
				sender.sendMessage(ChatColor.RED + String.format("Could not withdraw $%s from your town's bank!", amount));
			else {
				town.setBank(town.getBank() - amount);
				town.save();
				sender.sendMessage(ChatColor.GREEN + String.format("Successfully withdrew $%s from your town's bank! New balance: $%s", amount, town.getBank()));
			}
		} else {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Town t = TownManager.getInstance().getTownOfPlayer(p.getUniqueId());
				if (t != null) {
					if (t.getMayor().equals(p.getUniqueId())) {
						if (args[0].equalsIgnoreCase(t.getName())) {
							if (!checkPerm(sender, CommandData.Open))
								return;

							t.getInventory().openMenu(p);
							return;
						}
					} else {
						sender.sendMessage(ChatColor.RED + "You must be the town's mayor to use this command!");
						return;
					}
				}
			}
			sender.sendMessage(ChatColor.RED + "Invalid command!" + (checkPerm(sender, CommandData.Help) ? " Try using /t help for more info!" : ""));
			return;
		}
	}

	private static String getTownShortListing(Town town, int rank) {
		StringBuilder sb = new StringBuilder();

		if (town == null)
			return "NULL";

		sb.append(String.format("Rank: %s", rank));
		sb.append(ChatColor.RESET + " | ");
		sb.append(String.format("Name: %s", town.getName()));
		sb.append(ChatColor.RESET + " | ");
		sb.append(String.format("Mayor: %s", Bukkit.getOfflinePlayer(town.getMayor()).getName()));
		sb.append(ChatColor.RESET + " | ");
		sb.append(String.format("Size: %s (%s)", town.getTownType().getReadable(), town.getPlots().size()));
		sb.append(ChatColor.RESET + " | ");
		sb.append(String.format("Population: %s", town.getResidents().size()));

		return sb.toString();
	}

	static boolean hasPermission(CommandSender sender, String node) {
		return sender.hasPermission(node) || sender.isOp() || sender.hasPermission("tnt.*");
	}

	/**
	 * Checks the permission, and sends a message if a permission is not granted.
	 *
	 * @return if the player has permission.
	 * */
	private boolean checkPerm(CommandSender sender, CommandData command) {
		boolean b = hasPermission(sender, command.getPermission());
		if (!b)
			sendNoPermissionMessage(sender);
		return b;
	}

	private static void sendNoPermissionMessage(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "No permission!");
	}

}