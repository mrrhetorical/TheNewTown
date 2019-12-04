package com.rhetorical.town.towns.invite;

import java.util.UUID;

public class InviteRequest {

	private final UUID requester;
	private final UUID recipient;

	private final String town;

	InviteRequest(UUID requester, UUID recipient, String town) {
		this.requester = requester;
		this.recipient = recipient;
		this.town = town;
	}

	public UUID getRequester() {
		return requester;
	}

	public UUID getRecipient() {
		return recipient;
	}

	public String getTown() {
		return town;
	}

}
