package com.rhetorical.town.towns;

import com.rhetorical.town.TheNewTown;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class BorderManager {

	protected class BorderSettings {
		private Color color;
		private int height;

		protected BorderSettings(Color c, int h) {
			color = c;
			height = h;
		}


		public Color getColor() {
			return color;
		}

		public int getHeight() {
			return height;
		}
	}

	private static BorderManager instance;

	private Set<Player> players = new HashSet<>();

	private BorderSettings self, neutral, friendly, hostile;

	private BorderManager() {
		FileConfiguration config = TheNewTown.getInstance().getConfig();

		self = new BorderSettings (Color.fromRGB(config.getInt("border.self.color")), clamp(config.getInt("border.self.height"), 1, 5));
		friendly = new BorderSettings (Color.fromRGB(config.getInt("border.friendly.color")), clamp(config.getInt("border.friendly.height"), 1, 5));
		neutral = new BorderSettings (Color.fromRGB(config.getInt("border.neutral.color")), clamp(config.getInt("border.neutral.height"), 1, 5));
		hostile = new BorderSettings (Color.fromRGB(config.getInt("border.hostile.color")), clamp(config.getInt("border.hostile.height"), 1, 5));
	}

	public static BorderManager getInstance() {
		if (instance == null)
			instance = new BorderManager();

		return instance;
	}

	public Set<Player> getPlayers() {
		return players;
	}

	public void showBorder(Player p) {
		for (Town t : TownManager.getInstance().getTowns().values())
			t.showBorder(p);
	}

	public void autoShowBorder(Player p) {
		if (getPlayers().contains(p))
			getPlayers().remove(p);
		else {
			getPlayers().add(p);
			startBorderTask(p);
		}
	}

	public boolean isAutoShowBorder(Player p) {
		return getPlayers().contains(p);
	}

	private void startBorderTask(Player p) {
		BukkitRunnable br = new BukkitRunnable() {
			@Override
			public void run() {
				if (!getPlayers().contains(p)) {
					cancel();
					return;
				}
				showBorder(p);
			}
		};

		br.runTaskTimer(TheNewTown.getInstance(), 0L, 15L);
	}

	public BorderSettings getSelf() {
		return self;
	}

	public BorderSettings getNeutral() {
		return neutral;
	}

	public BorderSettings getFriendly() {
		return friendly;
	}

	public BorderSettings getHostile() {
		return hostile;
	}


	private static int clamp(int value, int min, int max) {
		return value > max ? max : value < min ? min : value;
	}
}
