package com.rhetorical.town.towns;

import com.rhetorical.town.TheNewTown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;

public class MapManager implements Listener {

	private static MapManager instance;

	private Set<Player> players = new HashSet<>();

	private MapManager() {
		Bukkit.getServer().getPluginManager().registerEvents(this, TheNewTown.getInstance());
	}

	public static MapManager getInstance() {
		if (instance == null)
			instance = new MapManager();

		return instance;
	}

	public Set<Player> getPlayers() {
		return players;
	}

	public void showMap(Player t) {
		int x = t.getLocation().getChunk().getX() - 15,
				y = t.getLocation().getChunk().getZ() - 3;

		World world = t.getLocation().getWorld();
		if (world == null) {
			Bukkit.getLogger().warning(String.format("Could not get world of player %s!", t.getDisplayName()));
			return;
		}

		Town town = TownManager.getInstance().getTownOfPlayer(t.getUniqueId());

		Chunk chunk;

		for (int r = 7; r >= 0; r--) {
			StringBuilder sb = new StringBuilder();
			for (int c = 0; c < 31; c++) {
				chunk = world.getChunkAt(x + c, y + r);
				if (town != null && town.getPlot(chunk) != null)
					sb.append(ChatColor.GREEN);
				else if (TownManager.getInstance().isChunkClaimed(chunk))
					sb.append(ChatColor.GOLD);
				else
					sb.append(ChatColor.GRAY);

				if (r == 3 && c == 15)
					sb.append("O");
				else
					sb.append("+");

			}
			t.sendMessage(sb.toString());
		}
	}

	public void autoShowMap(Player t) {
		if (getPlayers().contains(t))
			getPlayers().remove(t);
		else
			getPlayers().add(t);
	}

	public boolean isAutoShowMap(Player t) {
		return getPlayers().contains(t);
	}

	@EventHandler
	public void onChunkChange(PlayerMoveEvent e) {
		if (e.getTo().getChunk().equals(e.getFrom().getChunk()))
			return;

		if (isAutoShowMap(e.getPlayer()))
			showMap(e.getPlayer());
	}
}
