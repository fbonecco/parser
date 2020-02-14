package com.rocketinsights.core.event;

import java.util.LinkedList;
import java.util.List;

import com.rocketinsights.core.event.handler.EventHandler;
import com.rocketinsights.core.exception.ProcessingException;

public class SimpleEventLog implements EventLog {

	private List<EventHandler> handlers;

	public SimpleEventLog() {
		this.handlers = new LinkedList<>();
	}

	@Override
	public void push(Event event) throws ProcessingException {
		for (EventHandler handler : handlers) {
			try {
				handler.handle(event);
			} catch (Exception e) {
				throw new ProcessingException(String.format("An error occurred while handling the event [%s]", event),
						e);
			}
		}
	}

	@Override
	public void addHandler(EventHandler eventHandler) {
		handlers.add(eventHandler);

	}

}
