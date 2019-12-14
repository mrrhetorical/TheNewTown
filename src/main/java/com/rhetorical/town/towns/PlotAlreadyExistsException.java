package com.rhetorical.town.towns;

import org.bukkit.Chunk;

class PlotAlreadyExistsException extends Exception {

	enum FailReason {
		ALREADY_CLAIMED("Chunk already claimed!"), REGION_PROTECTION("Chunk is protected!");

		private String reason;

		FailReason(String reason) {
			this.reason	= reason;
		}

		@Override
		public String toString() {
			return reason;
		}
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
