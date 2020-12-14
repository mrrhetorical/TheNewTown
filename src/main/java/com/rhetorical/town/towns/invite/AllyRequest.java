package com.rhetorical.town.towns.invite;

public class AllyRequest {

	private final String requester;
	private final String recipient;

	AllyRequest(String requester, String recipient) {
		this.requester = requester;
		this.recipient = recipient;
	}

	public String getRequester() {
		return requester;
	}

	public String getRecipient() {
		return recipient;
	}

}
