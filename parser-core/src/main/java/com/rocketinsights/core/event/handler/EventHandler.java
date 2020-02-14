package com.rocketinsights.core.event.handler;

import com.rocketinsights.core.event.Event;

/**
 * Models an object that can handle {@link Event}s
 * 
 * @author fbonecco
 *
 */
public interface EventHandler {

	public void handle(Event event) throws Exception;

}
