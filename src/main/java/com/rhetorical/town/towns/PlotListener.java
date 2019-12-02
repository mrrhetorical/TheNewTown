package com.rhetorical.town.towns;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

class PlotListener implements Listener {

	private Plot plot;

	PlotListener(Plot plot) {
		setPlot(plot);
	}

	public Plot getPlot() {
		return plot;
	}

	private void setPlot(Plot value) {
		plot = value;
	}

	@EventHandler
	public void onPlayerEnterExitPlot(PlayerMoveEvent e) {

		//Leaving
		if (!plot.isInPlot(e.getTo()) && plot.isInPlot(e.getFrom())) {
			e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("Leaving %s", TownManager.getInstance().getTown(plot.getTown()).getName())));
		}
		//Entering
		else if (plot.isInPlot(e.getTo()) && !plot.isInPlot(e.getFrom())) {
			e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("Entering %s", TownManager.getInstance().getTown(plot.getTown()).getName())));
		} else
			return;
	}
}

