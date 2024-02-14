package com.ten31f.solutions.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

}
