package com.ten31f.solutions.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Event {

	private String description;
	private LocalDateTime localDateTime;

	public Event(String description) {
		setDescription(description);
		setLocalDateTime(LocalDateTime.now());
	}

	public Event(long millis, String description) {
		setDescription(description);
		Instant instant = Instant.ofEpochMilli(millis);
		setLocalDateTime(instant.atZone(ZoneId.systemDefault()).toLocalDateTime());
	}

	public String getDescription() {
		return description;
	}

	private void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}

	private void setLocalDateTime(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}
}
