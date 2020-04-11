package com.rhetorical.town.util;

import com.rhetorical.town.TheNewTown;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class EnterMessageUtil {

	public static void sendMessage(Player p, String message) {
		EnterMessageLocation loc = TheNewTown.getInstance().getEnterMessageLocation();
		switch(loc) {
			case TITLE:
				p.sendTitle(message, "", 10, 20, 10);
				break;
			case SUBTITLE:
				p.sendTitle("", message, 10, 20, 10);
				break;
			case ACTIONBAR:
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
				break;
			default:
				p.sendMessage(message);
				break;
		}
	}
}
