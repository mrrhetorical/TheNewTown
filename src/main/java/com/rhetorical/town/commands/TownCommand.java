package com.rhetorical.town.commands;

import com.rhetorical.town.towns.Town;
import com.rhetorical.town.towns.TownManager;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class TownCommand {

	enum CommandData {

		Help("/t help {page} - Shows the help messages for the given page, (or first page if none given).", "tnt.help"),
		Create("/t create [name] - Claims the plot you're standing in and creates the new town.", "tnt.create"),
		Delete("/t delete [name] [name] - Deletes the town as mayor. Input town name twice to confirm. (m)", "tnt.delete"),
		Join("/t join [town name] - Attempts to join the town with the given name.", "tnt.join"),
		Invite("/t invite [player] - Invites the given player to your town. (m)", "tnt.invite"),
		Claim("/t claim - Claims the plot you're standing in for your town. (m)", "tnt.claim"),
		Unclaim("/t unclaim - Unclaims the plot you're standing in from your town. (m)", "tnt.unclaim"),
		Sell("/t sell [cost] - Sells the current plot for the given price. A cost of -1 removes from market. (m)", "tnt.sell"),
		Buy("/t lease - Leases the current plot from the town.", "tnt.lease"),
		Flag("/t flag [plot/town] [flag] [true/false] - Sets the flag for the given plot or town. (m)", "tnt.flag"),
		Info("/t info [town] - Shows you info about the given town.", "tnt.info"),
		Flags("/t flag {args} - Set flags for the town. (m)", "tnt.flag"),
		Here("/t here - Checks current plot to see who it belongs to.", "tnt.here"),
		List("/t list [page] - Lists all the towns, their type, their mayor, and they population.", "tnt.list");

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

	private String pageHeader = "##### [TheNewTown Help %s/%s] #####";

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

			for (int i = (page - 1) * messagesPerPage; i < roof && i < CommandData.values().length; i++) {
				if (hasPermission(sender, messages[i].getPermission()))
					sender.sendMessage(messages[i].getMessage());
				else
					roof++;
			}

		} else if (args[0].equalsIgnoreCase("create")) {
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
		} else if (args[0].equalsIgnoreCase("delete")) {
			if (!checkPerm(sender, CommandData.Delete))
				return;

			if (args.length != 3) {
				sender.sendMessage(ChatColor.RED + "Improper usage! Correct usage: /t delete [name] [name]");
				return;
			}

			if (sender.isOp() || sender.hasPermission("tnt.admin") || sender instanceof ConsoleCommandSender) {
				String town = args[1];
				Town t = TownManager.getInstance().getTown(town);
				if (t == null) {
					sender.sendMessage(ChatColor.RED + "No such town exists with that name!");
					return;
				}

				String town2 = args[2];

				if (!town.equals(town2)) {
					sender.sendMessage(ChatColor.RED + "Could not delete town! Town names do not match!");
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

			String a = args[1],
					b = args[2];

			if (!a.equals(t.getName())) {
				sender.sendMessage(ChatColor.RED + "Could not delete town! You can only delete your own town!");
				return;
			}

			if (!a.equals(b)) {
				sender.sendMessage(ChatColor.RED + "Could not delete town! Town names do not match!");
				return;
			}

			TownManager.getInstance().deleteTown(a);
			sender.sendMessage(ChatColor.GREEN + "Successfully deleted town!");
			return;
		} else if (args[0].equalsIgnoreCase("claim")) {
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

			Chunk chunk = p.getLocation().getChunk();

			if (TownManager.getInstance().isChunkClaimed(chunk)) {
				p.sendMessage(ChatColor.RED + "That chunk is already claimed!");
				return;
			}

			if (town.addPlot(chunk)) {
				p.sendMessage(ChatColor.GREEN + "Successfully claimed plot for your town!");
				town.save();
			} else {
				p.sendMessage(ChatColor.RED + "Could not claim plot!");
			}

			return;
		} else if (args[0].equalsIgnoreCase("unclaim")) {
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
		} else if (args[0].equalsIgnoreCase("here")) {
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
	}

	private boolean hasPermission(CommandSender sender, String node) {
		return sender.hasPermission(node) || sender.isOp() || sender.hasPermission("tnt.*");
	}

	private boolean isConsole(CommandSender sender) {
		return sender instanceof ConsoleCommandSender;
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

	private void sendNoPermissionMessage(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "No permission!");
	}

}
