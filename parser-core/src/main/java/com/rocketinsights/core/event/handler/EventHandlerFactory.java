package com.rocketinsights.core.event.handler;

import java.io.IOException;
import java.io.Writer;

import com.rocketinsights.core.parser.SupportedFormat;

public interface EventHandlerFactory {

	public EventHandler createEventHandler(SupportedFormat format, Writer writer) throws IOException;

}
