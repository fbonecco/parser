package com.rocketinsights.core.event;

import java.util.Objects;

public class Event {

	private EventType eventType;

	private String name;

	private String data;

	public Event(EventType eventType) {
		super();
		this.eventType = eventType;
	}

	public Event(EventType eventType, String name, String data) {
		super();
		this.eventType = eventType;
		this.name = name;
		this.data = data;
	}

	public EventType getEventType() {
		return eventType;
	}

	public String getName() {
		return name;
	}

	public String getData() {
		return data;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (this == o)
			return true;
		if (!o.getClass().equals(getClass()))
			return false;
		Event obj = (Event) o;
		return Objects.equals(eventType, obj.eventType) && Objects.equals(name, obj.name)
				&& Objects.equals(data, obj.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(eventType, name, data);
	}

	@Override
	public String toString() {
		return "Event [eventType=" + eventType + ", name=" + name + ", data=" + data + "]";
	}
}
