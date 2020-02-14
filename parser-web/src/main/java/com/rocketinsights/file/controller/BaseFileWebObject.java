package com.rocketinsights.file.controller;

import java.time.Instant;
import java.util.UUID;

public abstract class BaseFileWebObject {

	protected UUID id;
	protected String filename;
	protected Instant created;
	protected Instant lastModified;
	protected String path;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Instant getCreated() {
		return created;
	}

	public void setCreated(Instant created) {
		this.created = created;
	}

	public Instant getLastModified() {
		return lastModified;
	}

	public void setLastModified(Instant lastModified) {
		this.lastModified = lastModified;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
