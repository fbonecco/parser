package com.rocketinsights.core.event;

import com.rocketinsights.core.event.handler.EventHandler;
import com.rocketinsights.core.exception.ProcessingException;

public interface EventLog {

	public void push(Event event) throws ProcessingException;

	public void addHandler(EventHandler eventHandler);
}
