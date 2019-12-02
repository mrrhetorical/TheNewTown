package com.rhetorical.town.towns;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@SuppressWarnings("Duplicates")
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
		if (!getPlot().isInPlot(e.getTo()) && getPlot().isInPlot(e.getFrom())) {
			Town owner = TownManager.getInstance().getTown(getPlot().getTown());
			if (owner.isChunkClaimed(e.getTo().getChunk()) && owner.isChunkClaimed(e.getFrom().getChunk()))
				return;
			e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("Leaving %s", getPlot().getTown())));
		}
		//Entering
		else if (getPlot().isInPlot(e.getTo()) && !getPlot().isInPlot(e.getFrom())) {
			Town owner = TownManager.getInstance().getTown(getPlot().getTown());
			if (owner.isChunkClaimed(e.getTo().getChunk()) && owner.isChunkClaimed(e.getFrom().getChunk()))
				return;
			e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("Entering %s", getPlot().getTown())));
		} else
			return;
	}

	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent e) {
		if (!getPlot().isInPlot(e.getBlock().getLocation()))
			return;

		Town town = TownManager.getInstance().getTown(plot.getTown());

		if (getPlot().getLeaser() != null && !e.getPlayer().getUniqueId().equals(getPlot().getLeaser()) && !e.getPlayer().getUniqueId().equals(town.getMayor())) {
			e.setCancelled(true);
			return;
		}

		if (!town.getResidents().contains(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onPlayerPlaceBlockEvent(BlockPlaceEvent e) {
		if (!getPlot().isInPlot(e.getBlock().getLocation()))
			return;

		Town town = TownManager.getInstance().getTown(plot.getTown());

		if (getPlot().getLeaser() != null && !e.getPlayer().getUniqueId().equals(getPlot().getLeaser()) && !e.getPlayer().getUniqueId().equals(town.getMayor())) {
			e.setCancelled(true);
			return;
		}

		if (!town.getResidents().contains(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
			return;
		}
	}
}

