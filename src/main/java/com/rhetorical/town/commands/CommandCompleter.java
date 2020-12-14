package com.rhetorical.town.commands;

import com.rhetorical.town.towns.Town;
import com.rhetorical.town.towns.TownManager;
import com.rhetorical.town.towns.flags.TownFlag;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandCompleter implements TabCompleter {

	@SuppressWarnings("Duplicates")
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		if (!cmd.getName().equalsIgnoreCase("town") && !cmd.getName().equals("t"))
			return null;

		List<String> list = new ArrayList<>();

		//inputting first argument
		if (args.length == 1) {

			if (checkPerm(sender, TownCommand.CommandData.Help) && "help".startsWith(args[0]))
				list.add("help");

			if (checkPerm(sender, TownCommand.CommandData.Create) && "create".startsWith(args[0]))
				list.add("create");

			if (checkPerm(sender, TownCommand.CommandData.Delete) && "delete".startsWith(args[0]))
				list.add("delete");

			if (checkPerm(sender, TownCommand.CommandData.Join) && "join".startsWith(args[0]))
				list.add("join");

			if (checkPerm(sender, TownCommand.CommandData.Invite) && "invite".startsWith(args[0]))
				list.add("invite");

			if (checkPerm(sender, TownCommand.CommandData.Claim) && "claim".startsWith(args[0]))
				list.add("claim");

			if (checkPerm(sender, TownCommand.CommandData.Unclaim) && "unclaim".startsWith(args[0]))
				list.add("unclaim");

			if (checkPerm(sender, TownCommand.CommandData.Sell) && "sell".startsWith(args[0]))
				list.add("sell");

			if (checkPerm(sender, TownCommand.CommandData.Buy) && "buy".startsWith(args[0]))
				list.add("lease");

			if (checkPerm(sender, TownCommand.CommandData.Flag) && "flag".startsWith(args[0]))
				list.add("flag");

			if (checkPerm(sender, TownCommand.CommandData.Flags) && "flags".startsWith(args[0]))
				list.add("flags");

			if (checkPerm(sender, TownCommand.CommandData.Tax) && "tax".startsWith(args[0]))
				list.add("tax");

			if (checkPerm(sender, TownCommand.CommandData.Info) && "info".startsWith(args[0]))
				list.add("info");

			if (checkPerm(sender, TownCommand.CommandData.Here) && "here".startsWith(args[0]))
				list.add("here");

			if (checkPerm(sender, TownCommand.CommandData.List) && "list".startsWith(args[0]))
				list.add("list");

			if (checkPerm(sender, TownCommand.CommandData.Leave) && "leave".startsWith(args[0]))
				list.add("leave");

			if (checkPerm(sender, TownCommand.CommandData.SetHome) && "sethome".startsWith(args[0]))
				list.add("sethome");

			if (checkPerm(sender, TownCommand.CommandData.Home) && "home".startsWith(args[0]))
				list.add("home");

			if (checkPerm(sender, TownCommand.CommandData.Kick) && "kick".startsWith(args[0]))
				list.add("kick");

			if (checkPerm(sender, TownCommand.CommandData.MAP) && "map".startsWith(args[0]))
				list.add("map");

			if (checkPerm(sender, TownCommand.CommandData.Border) && "border".startsWith(args[0]))
				list.add("border");

			if (checkPerm(sender, TownCommand.CommandData.Balance) && "balance".startsWith(args[0]))
				list.add("balance");

			if (checkPerm(sender, TownCommand.CommandData.Withdraw) && "withdraw".startsWith(args[0]))
				list.add("withdraw");

			if (checkPerm(sender, TownCommand.CommandData.Deposit) && "deposit".startsWith(args[0]))
				list.add("deposit");

			if (checkPerm(sender, TownCommand.CommandData.Ally) && "ally".startsWith(args[0]))
				list.add("ally");

			if (checkPerm(sender, TownCommand.CommandData.AllyList) && "allies".startsWith(args[0]))
				list.add("allies");

			if (sender instanceof Player) {
				if (!sender.isOp() || sender.hasPermission("t.admin")) {
					Town town = TownManager.getInstance().getTownOfPlayer(((Player) sender).getUniqueId());
					if (town != null)
						list.add(town.getName());
				} else
					list.addAll(getTownList());
			} else if (sender instanceof ConsoleCommandSender)
				list.addAll(getTownList());

			return list;
		}
		//inputting second argument
		else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("delete") && checkPerm(sender, TownCommand.CommandData.Delete)) {

				if (sender instanceof Player) {
					if (!sender.isOp() || sender.hasPermission("t.admin")) {
						Town town = TownManager.getInstance().getTownOfPlayer(((Player) sender).getUniqueId());
						if (town != null && town.getName().startsWith(args[1])) {
							list.add(town.getName());
						}
					} else {
						for (String t : getTownList()) {
							if (t.startsWith(args[1]))
								list.add(t);
						}
					}
				} else if (sender instanceof ConsoleCommandSender)
					for (String t : getTownList()) {
						if (t.startsWith(args[1]))
							list.add(t);
					}

				return list;
			}

			if ((args[0].equalsIgnoreCase("info") && checkPerm(sender, TownCommand.CommandData.Info))
					|| (args[0].equalsIgnoreCase("join") && checkPerm(sender, TownCommand.CommandData.Join))) {
				for (String t : getTownList()) {
					if (t.startsWith(args[1]))
						list.add(t);
				}
				return list;
			}

			if (args[0].equalsIgnoreCase("map") && checkPerm(sender, TownCommand.CommandData.MAP) && "auto".startsWith(args[1])) {
				list.add("auto");
				return list;
			}

			if (args[0].equalsIgnoreCase("border") && checkPerm(sender, TownCommand.CommandData.Border) && "auto".startsWith(args[1])) {
				list.add("auto");
				return list;
			}

			if (args[0].equalsIgnoreCase("lease") && checkPerm(sender, TownCommand.CommandData	.Buy) && "release".startsWith(args[1])) {
				list.add("release");
				return list;
			}

			if (args[0].equalsIgnoreCase("flag") && checkPerm(sender, TownCommand.CommandData.Flag)) {
				if ("plot".startsWith(args[1]))
					list.add("plot");
				if ("town".startsWith(args[1]))
					list.add("town");
				return list;
			}
		}
		else if (args.length == 3) {
			if ((args[1].equalsIgnoreCase("plot") || args[1].equalsIgnoreCase("town")) && checkPerm(sender, TownCommand.CommandData.Flag)) {
				for (TownFlag flag : TownFlag.values())
					if (flag.toString().toLowerCase().startsWith(args[2]))
						list.add(flag.toString().toLowerCase());

				return list;
			}
		}
		else if (args.length == 4) {
			if (args[0].equalsIgnoreCase("flag") && (args[1].equalsIgnoreCase("plot") || args[1].equalsIgnoreCase("town")) && TownFlag.stringValues().contains(args[2])) {
				if ("true".startsWith(args[3]))
					list.add("true");
				if ("false".startsWith(args[3]))
					list.add("false");
				if (args[1].equalsIgnoreCase("plot") && "clear".startsWith(args[3]))
					list.add("clear");
				return list;
			}
		}

		return list;
	}

	private boolean checkPerm(CommandSender sender, TownCommand.CommandData data) {
		return TownCommand.hasPermission(sender, data.getPermission());
	}

	private List<String> getTownList() {
		return new ArrayList<>(TownManager.getInstance().getTowns().keySet());
	}

}
