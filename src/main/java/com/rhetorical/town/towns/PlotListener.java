package com.rhetorical.town.towns;

import com.rhetorical.town.towns.flags.TownFlag;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
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
		//Entering
		if (getPlot().isInPlot(e.getTo()) && !getPlot().isInPlot(e.getFrom())) {
			Town owner = TownManager.getInstance().getTown(getPlot().getTown());
			if (owner.isChunkClaimed(e.getTo().getChunk()) && owner.isChunkClaimed(e.getFrom().getChunk()))
				return;
			e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("Entering %s", getPlot().getTown())));
		}
		//Leaving
		else if (!getPlot().isInPlot(e.getTo()) && getPlot().isInPlot(e.getFrom())) {
			if (TownManager.getInstance().isChunkClaimed(e.getTo().getChunk()))
				return;
			Town owner = TownManager.getInstance().getTown(getPlot().getTown());
			if (owner.isChunkClaimed(e.getTo().getChunk()) && owner.isChunkClaimed(e.getFrom().getChunk()))
				return;
			e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format("Leaving %s", getPlot().getTown())));
		} else
			return;
	}

	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent e) {
		if (!getPlot().isInPlot(e.getBlock().getLocation()))
			return;

		Town town = TownManager.getInstance().getTown(plot.getTown());

		if (!town.getResidents().contains(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
			return;
		}

		if (!e.getPlayer().getUniqueId().equals(getPlot().getLeaser()) && !e.getPlayer().getUniqueId().equals(town.getMayor())) {
			if (!getPlot().getFlag(TownFlag.ALLOW_MODIFICATION))
				e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onPlayerPlaceBlockEvent(BlockPlaceEvent e) {
		if (!getPlot().isInPlot(e.getBlock().getLocation()))
			return;

		Town town = TownManager.getInstance().getTown(plot.getTown());

		if (!town.getResidents().contains(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
			return;
		}

		if (!e.getPlayer().getUniqueId().equals(getPlot().getLeaser()) && !e.getPlayer().getUniqueId().equals(town.getMayor())) {
			if (!getPlot().getFlag(TownFlag.ALLOW_MODIFICATION))
				e.setCancelled(true);
			return;
		}

	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		if (e.getClickedBlock() == null)
			return;

		if (!getPlot().isInPlot(e.getClickedBlock().getLocation()))
			return;

		Town town = TownManager.getInstance().getTown(plot.getTown());

		if (!town.getResidents().contains(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
			return;
		}

		if (!e.getPlayer().getUniqueId().equals(getPlot().getLeaser()) && !e.getPlayer().getUniqueId().equals(town.getMayor())) {
			if (!getPlot().getFlag(TownFlag.ALIEN_INTERACT))
				e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onPlayerDamagePlayer(EntityDamageByEntityEvent e) {
		Player damager, victim;

		if (!(e.getDamager() instanceof Player) && !(e.getEntity() instanceof Player)) {
			if (e.getDamager() instanceof Projectile) {
				Projectile projectile = (Projectile) e.getDamager();
				if (!(projectile.getShooter() instanceof Player))
					return;
				damager = (Player) projectile.getShooter();
			} else
				return;
		} else {
			damager = (Player) e.getDamager();
		}

		victim = (Player) e.getEntity();

		if (!getPlot().isInPlot(damager.getLocation()) && !getPlot().isInPlot(victim.getLocation()))
			return;

		if (getPlot().getFlag(TownFlag.NO_PVP))
			e.setCancelled(true);
	}

	@EventHandler
	public void onExplosion(BlockExplodeEvent e) {
		if (getPlot().isInPlot(e.getBlock().getLocation()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onExplosion(EntityExplodeEvent e) {
		if (getPlot().isInPlot(e.getLocation()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onPickupEvent(EntityPickupItemEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;

		Player p = (Player) e.getEntity();

		if (!getPlot().isInPlot(p.getLocation()) && !getPlot().isInPlot(e.getItem().getLocation()))
			return;

		if (!p.getUniqueId().equals(getPlot().getLeaser()) && !p.getUniqueId().equals(getPlot().getOwner())) {
			if (!getPlot().getFlag(TownFlag.ALLOW_PICKUP))
				e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onDropEvent(EntityDropItemEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;

		Player p = (Player) e.getEntity();

		if (!getPlot().isInPlot(p.getLocation()) && !getPlot().isInPlot(e.getItemDrop().getLocation()))
			return;

		if (!p.getUniqueId().equals(getPlot().getLeaser()) && !p.getUniqueId().equals(getPlot().getOwner())) {
			if (!getPlot().getFlag(TownFlag.ALLOW_DROP))
				e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onAnimalAbsue(EntityDamageByEntityEvent e) {
		if (!getPlot().isInPlot(e.getDamager().getLocation()) && !getPlot().isInPlot(e.getEntity().getLocation()))
			return;

		if (!(e.getEntity() instanceof Animals) || (e.getEntity() instanceof PolarBear))
			return;

		if (!(e.getDamager() instanceof Player))
			return;

		Player p = (Player) e.getDamager();

		if (!p.getUniqueId().equals(getPlot().getLeaser()) && !p.getUniqueId().equals(getPlot().getOwner())) {
			if (!getPlot().getFlag(TownFlag.ANIMAL_ABUSE))
				e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onMobSpawn(EntitySpawnEvent e) {
		if (!(e instanceof Monster))
			return;

		if (!getPlot().isInPlot(e.getLocation()))
			return;

		if (!getPlot().getFlag(TownFlag.MOB_SPAWNING))
			e.setCancelled(true);
	}

}

