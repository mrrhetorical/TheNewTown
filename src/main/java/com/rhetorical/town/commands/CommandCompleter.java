package com.rhetorical.town.commands;

import com.rhetorical.town.towns.TownManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CommandCompleter implements TabCompleter {

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
			return list;
		}
		//inputting second argument
		else if (args.length == 2) {

		}

		return list;
	}

	private boolean checkPerm(CommandSender sender, TownCommand.CommandData data) {
		return TownCommand.checkPerm(sender, data);
	}

	private List<String> getTownList() {
		return new ArrayList<>(TownManager.getInstance().getTowns().keySet());
	}

}
