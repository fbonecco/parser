package com.rocketinsights.core.event.handler;

import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

import com.rocketinsights.core.event.Event;
import com.rocketinsights.core.event.EventType;
import com.rocketinsights.core.exception.ProcessingException;

/**
 * Translates series of {@link Event}s to properties-like structure.
 * 
 * @author fbonecco
 *
 */
public class PropertyEventHandler extends DocumentEventHandler {

	private static final String NEW_LINE = "\n";

	private Stack<String> currentProperty;

	public PropertyEventHandler(Writer writer) {
		super(writer);
	}

	@Override
	protected void handleDocumentStarted(Event event, Writer writer) throws IOException, ProcessingException {
		currentProperty = new Stack<>();
	}

	@Override
	protected void handleDocumentEnded(Event event, Writer writer) throws IOException, ProcessingException {
	}

	@Override
	protected void handleNodeOpened(Event event, Writer writer) throws IOException, ProcessingException {
		StringBuilder node = new StringBuilder();
		if (last != null && EventType.NODE_CLOSED.equals(last.getEventType())) {
			node.append(NEW_LINE);
			currentProperty.forEach(e -> {
				if (node.length() > NEW_LINE.length()) {
					node.append(".");
				}
				node.append(e);
			});
		}
		if (currentProperty.size() > 0) {
			node.append(".");
		}
		node.append(event.getName());
		writer.write(node.toString());
		currentProperty.add(event.getName());
	}

	@Override
	protected void handleNodeClosed(Event event, Writer writer) throws IOException, ProcessingException {
		if (!currentProperty.isEmpty() && currentProperty.peek().equals(event.getName())) {
			currentProperty.pop();
		}
		if (currentProperty.isEmpty()) {
			currentProperty.clear();
		}
	}

	@Override
	protected void handleValueAdded(Event event, Writer writer) throws IOException, ProcessingException {
		if (!currentProperty.isEmpty()) {
			StringBuilder value = new StringBuilder();
			value.append(" = ");
			value.append("\"");
			value.append(event.getData());
			value.append("\"");

			writer.write(value.toString());
		}
	}

}
