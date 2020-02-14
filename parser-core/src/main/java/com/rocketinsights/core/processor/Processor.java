package com.rocketinsights.core.processor;

import com.rocketinsights.core.event.EventLog;

public interface Processor {

	public EventLog getEventBus();

}
