package com.rhetorical.town.towns.invite;

import com.rhetorical.town.TheNewTown;
import com.rhetorical.town.towns.Town;
import com.rhetorical.town.towns.TownManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InviteManager {

	private static InviteManager instance;

	private Set<InviteRequest> activeRequests = new HashSet<>();

	private InviteManager() {}

	public Set<InviteRequest> getActiveRequests() {
		return activeRequests;
	}

	public InviteRequest getRequestByRecipient(UUID recipient) {
		for (InviteRequest request : getActiveRequests())
			if (request.getRecipient().equals(recipient))
				return request;

		return null;
	}

	public static InviteManager getInstance() {
		if (instance == null)
			instance = new InviteManager();

		return instance;
	}

	public boolean generateRequest(UUID requester, UUID recipient, String town) {
		InviteRequest request = new InviteRequest(requester, recipient, town);

		Town t = TownManager.getInstance().getTown(town);
		if (town == null)
			return false;

		if (requester.equals(recipient) || t.getResidents().contains(recipient))
			return false;

		boolean response = getActiveRequests().add(request);

		if (response) {

			Player req = Bukkit.getPlayer(request.getRequester()),
					rec = Bukkit.getPlayer(request.getRecipient());

			if (req != null)
				req.sendMessage(ChatColor.GRAY + "You sent an invite to " + Bukkit.getOfflinePlayer(request.getRecipient()).getName() + " to join " + request.getTown() + "!");

			if (rec != null)
				rec.sendMessage(ChatColor.GRAY + "You received an invite from " + Bukkit.getOfflinePlayer(request.getRequester()).getName() + " to join " + request.getTown() + String.format("! Join with '/t join %s'!", request.getTown()));

			startTimeout(request);
		}

		return response;
	}

	public boolean tryAcceptRequest(UUID target, String town) {
		for (InviteRequest request : new HashSet<>(getActiveRequests())) {
			if (request.getRecipient().equals(target) && request.getTown().equalsIgnoreCase(town)) {
				getActiveRequests().remove(request);
				Town t = TownManager.getInstance().getTown(town);
				if (t != null)
					return t.addPlayer(target);
			}
		}
		return false;
	}

	private void startTimeout(InviteRequest request) {

		BukkitRunnable br = new BukkitRunnable() {
			@Override
			public void run() {
				getActiveRequests().remove(request);
				Player requester = Bukkit.getPlayer(request.getRequester()),
						recipient = Bukkit.getPlayer(request.getRecipient());

				if (requester != null)
					requester.sendMessage(ChatColor.GRAY + "Your invite to " + Bukkit.getOfflinePlayer(request.getRecipient()).getName() + " to join " + request.getTown() + " has expired!");
				if (recipient != null)
					recipient.sendMessage(ChatColor.GRAY + "Your invite from " + Bukkit.getOfflinePlayer(request.getRequester()).getName() + " to join " + request.getTown() + " has expired!");
			}
		};

		br.runTaskLater(TheNewTown.getInstance(), 2400L);

	}

}
