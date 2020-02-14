package com.rocketinsights.core.reader;

import java.io.IOException;
import java.nio.file.Path;

import com.rocketinsights.core.event.Event;
import com.rocketinsights.core.event.EventLog;
import com.rocketinsights.core.exception.ProcessingException;

/**
 * This interface models an object which has two responsibilities. Pulls data
 * from a file, then it parses that data, and ends pushing events {@link Event}
 * to a {@link EventLog} object. These series of events represent the content
 * within the file in an abstract way.
 * 
 * Readers are responsible for ensuring that the format of the input file is
 * correct. All the {@link Event} that implementations of this class push, are
 * expected to be in a logical order.
 * 
 * @author fbonecco
 *
 */
public interface Reader {

	public void read(Path path, EventLog eventLog) throws IOException, ProcessingException;
}
