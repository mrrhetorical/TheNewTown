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

			if (checkPerm(sender, TownCommand.CommandData.Help))
				list.add("help");

			if (checkPerm(sender, TownCommand.CommandData.Create))
				list.add("create");

			if (checkPerm(sender, TownCommand.CommandData.Delete))
				list.add("delete");

			if (checkPerm(sender, TownCommand.CommandData.Join))
				list.add("join");

			if (checkPerm(sender, TownCommand.CommandData.Invite))
				list.add("invite");

			if (checkPerm(sender, TownCommand.CommandData.Claim))
				list.add("claim");

			if (checkPerm(sender, TownCommand.CommandData.Unclaim))
				list.add("unclaim");

			if (checkPerm(sender, TownCommand.CommandData.Sell))
				list.add("sell");

			if (checkPerm(sender, TownCommand.CommandData.Buy))
				list.add("lease");

			if (checkPerm(sender, TownCommand.CommandData.Flag))
				list.add("flag");

			if (checkPerm(sender, TownCommand.CommandData.Flags))
				list.add("flags");

			if (checkPerm(sender, TownCommand.CommandData.Tax))
				list.add("tax");

			if (checkPerm(sender, TownCommand.CommandData.Info))
				list.add("info");

			if (checkPerm(sender, TownCommand.CommandData.Here))
				list.add("here");

			if (checkPerm(sender, TownCommand.CommandData.List))
				list.add("list");

			if (checkPerm(sender, TownCommand.CommandData.Leave))
				list.add("leave");

			if (checkPerm(sender, TownCommand.CommandData.SetHome))
				list.add("sethome");

			if (checkPerm(sender, TownCommand.CommandData.Home))
				list.add("home");

			if (checkPerm(sender, TownCommand.CommandData.Kick))
				list.add("kick");

			if (checkPerm(sender, TownCommand.CommandData.MAP))
				list.add("map");

			if (checkPerm(sender, TownCommand.CommandData.Border))
				list.add("border");

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
						if (town != null)
							list.add(town.getName());
					} else
						list.addAll(getTownList());
				} else if (sender instanceof ConsoleCommandSender)
					list.addAll(getTownList());

				return list;
			}

			if ((args[0].equalsIgnoreCase("info") && checkPerm(sender, TownCommand.CommandData.Info))
					|| (args[0].equalsIgnoreCase("join") && checkPerm(sender, TownCommand.CommandData.Join))) {
				list.addAll(getTownList());
				return list;
			}

			if (args[0].equalsIgnoreCase("map") && checkPerm(sender, TownCommand.CommandData.MAP)) {
				list.add("auto");
				return list;
			}

			if (args[0].equalsIgnoreCase("border") && checkPerm(sender, TownCommand.CommandData.Border)) {
				list.add("auto");
				return list;
			}

			if (args[0].equalsIgnoreCase("lease") && checkPerm(sender, TownCommand.CommandData	.Buy)) {
				list.add("release");
				return list;
			}

			if (args[0].equalsIgnoreCase("flag") && checkPerm(sender, TownCommand.CommandData.Flag)) {
				list.add("plot");
				list.add("town");
				return list;
			}
		}
		else if (args.length == 3) {
			if ((args[1].equalsIgnoreCase("plot") || args[1].equalsIgnoreCase("town")) && checkPerm(sender, TownCommand.CommandData.Flag)) {
				for (TownFlag flag : TownFlag.values())
					list.add(flag.toString().toLowerCase());

				return list;
			}
		}
		else if (args.length == 4) {
			if (args[0].equalsIgnoreCase("flag") && (args[1].equalsIgnoreCase("plot") || args[1].equalsIgnoreCase("town")) && TownFlag.stringValues().contains(args[2])) {
				list.add("true");
				list.add("false");
				if (args[1].equalsIgnoreCase("plot"))
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
