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
	private Set<AllyRequest> activeAllyRequests = new HashSet<>();

	private InviteManager() {}

	public Set<InviteRequest> getActiveRequests() {
		return activeRequests;
	}

	public Set<AllyRequest> getActiveAllyRequests() {
		return activeAllyRequests;
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
				if (!getActiveRequests().remove(request))
					return;
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

	public boolean generateRequest(String requester, String recipient) {
		AllyRequest request = new AllyRequest(requester, recipient);

		Town t = TownManager.getInstance().getTown(requester);
		Town b = TownManager.getInstance().getTown(recipient);
		if (t == null || b == null)
			return false;

		if (t.getAllies().contains(recipient) || b.getAllies().contains(requester))
			return false;

		boolean response = getActiveAllyRequests().add(request);

		if (response) {
			Player req = Bukkit.getPlayer(t.getMayor()),
					rec = Bukkit.getPlayer(b.getMayor());

			if (req != null)
				req.sendMessage(ChatColor.GRAY + "You sent an invite to " + recipient + " to ally with " + requester + "!");

			if (rec != null)
				rec.sendMessage(ChatColor.GRAY + "You received an invite from " + requester + " to ally with " + requester + String.format("! Join with '/t ally accept %s'!", requester));

			startTimeout(request);
		}

		return response;
	}

	private void startTimeout(AllyRequest request) {

		BukkitRunnable br = new BukkitRunnable() {
			@Override
			public void run() {
				if (!getActiveAllyRequests().remove(request))
					return;
				Player requester = Bukkit.getPlayer(TownManager.getInstance().getTown(request.getRequester()).getMayor());
				Player recipient = Bukkit.getPlayer(TownManager.getInstance().getTown(request.getRecipient()).getMayor());

				if (requester != null)
					requester.sendMessage(ChatColor.GRAY + "Your invite to " + Bukkit.getOfflinePlayer(request.getRecipient()).getName() + " to ally with " + request.getRequester() + " has expired!");
				if (recipient != null)
					recipient.sendMessage(ChatColor.GRAY + "Your invite from " + Bukkit.getOfflinePlayer(request.getRequester()).getName() + " to ally with " + request.getRecipient() + " has expired!");
			}
		};

		br.runTaskLater(TheNewTown.getInstance(), 2400L);

	}

	public boolean tryAcceptAllyRequest(String sender, String target) {
		for (AllyRequest request : new HashSet<>(getActiveAllyRequests())) {
			if (request.getRecipient().equals(target) && request.getRequester().equalsIgnoreCase(sender)) {
				getActiveAllyRequests().remove(request);
				Town t = TownManager.getInstance().getTown(sender);
				t.addAlly(target);
				Town t1 = TownManager.getInstance().getTown(target);
				t1.addAlly(sender);
				return true;
			}
		}

		return false;
	}

}
