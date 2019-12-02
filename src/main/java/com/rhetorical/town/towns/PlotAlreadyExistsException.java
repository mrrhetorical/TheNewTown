package com.rhetorical.town.towns;

import org.bukkit.Chunk;

class PlotAlreadyExistsException extends Exception {

	enum FailReason {
		ALREADY_CLAIMED, REGION_PROTECTION
	}

	private Chunk chunk;
	private FailReason failReason;

	PlotAlreadyExistsException(Chunk chunk, FailReason failReason) {
		setChunk(chunk);
		setFailReason(failReason);
	}

	private void setChunk(Chunk value) {
		chunk = value;
	}

	public Chunk getChunk() {
		return chunk;
	}

	public FailReason getFailReason() {
		return failReason;
	}

	private void setFailReason(FailReason value) {
		failReason = value;
	}


}
