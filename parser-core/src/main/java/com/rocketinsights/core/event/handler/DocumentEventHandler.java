package com.rocketinsights.core.event.handler;

import java.io.IOException;
import java.io.Writer;

import com.rocketinsights.core.event.Event;
import com.rocketinsights.core.event.EventType;
import com.rocketinsights.core.exception.ProcessingException;

/**
 * Base implementation of {@link EventHandler} for dealing with document-like
 * events of type {@link Event}.
 * 
 * @author fbonecco
 *
 */
public abstract class DocumentEventHandler implements EventHandler {

	protected Writer writer;

	protected Event last;

	protected boolean documentStarted = false;

	public DocumentEventHandler(Writer writer) {
		this.writer = writer;
		this.last = null;
	}

	/**
	 * Checks the type of the {@link Event} received, and delegates the execution to
	 * one of the handle* methods. Each of these methods will try to translate the
	 * events and to write them to a {@link Writer}.
	 */
	@Override
	public final void handle(Event event) throws ProcessingException {
		EventType eventType = event.getEventType();

		try {
			switch (eventType) {
			case DOC_STARTED:
				handleDocumentStarted(event, writer);
				documentStarted = true;
				break;
			case DOC_ENDED:
				handleDocumentEnded(event, writer);
				break;
			case NODE_OPENED:
				handleNodeOpened(event, writer);
				break;
			case NODE_CLOSED:
				handleNodeClosed(event, writer);
				break;
			case VALUE_ADDED:
				handleValueAdded(event, writer);
				break;
			default:
				break;
			}
			last = event;
		} catch (IOException e) {
			throw new ProcessingException("There was an error while processing events.", e);
		}
	}

	protected abstract void handleDocumentStarted(Event event, Writer writer) throws IOException, ProcessingException;

	protected abstract void handleDocumentEnded(Event event, Writer writer) throws IOException, ProcessingException;

	protected abstract void handleNodeOpened(Event event, Writer writer) throws IOException, ProcessingException;

	protected abstract void handleNodeClosed(Event event, Writer writer) throws IOException, ProcessingException;

	protected abstract void handleValueAdded(Event event, Writer writer) throws IOException, ProcessingException;

}
