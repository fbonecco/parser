package com.rocketinsights.core.event.handler;

import java.io.IOException;
import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import com.rocketinsights.core.event.Event;
import com.rocketinsights.core.exception.ProcessingException;

/**
 * Translates series of {@link Event}s to an XML structure.
 * 
 * @author fbonecco
 *
 */
public class XmlEventHandler extends DocumentEventHandler {

	private static final String ROOT_NODE = "root";
	private static final String VERSION = "1.0";
	private static final String ENCODING = "UTF-8";
	private static final String TAB = "\t";

	private XMLStreamWriter streamWriter;

	public XmlEventHandler(Writer writer) throws IOException {
		super(writer);
		init();
	}

	private void init() throws IOException {
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		try {
			streamWriter = new IndentingXMLStreamWriter(outputFactory.createXMLStreamWriter(writer));
			((IndentingXMLStreamWriter) streamWriter).setIndentStep(TAB);
		} catch (XMLStreamException e) {
			throw new IOException("Can't setup handler.", e);
		}

	}

	@Override
	protected void handleDocumentStarted(Event event, Writer writer) throws IOException, ProcessingException {
		try {
			streamWriter.writeStartDocument(ENCODING, VERSION);
			streamWriter.writeStartElement(ROOT_NODE);
		} catch (XMLStreamException e) {
			throw new ProcessingException(String.format("There was an error handling the event [%s].", event), e);
		}

	}

	@Override
	protected void handleDocumentEnded(Event event, Writer writer) throws IOException, ProcessingException {
		try {
			streamWriter.writeEndElement();
		} catch (XMLStreamException e) {
			throw new ProcessingException(String.format("There was an error handling the event [%s].", event), e);
		}
	}

	@Override
	protected void handleNodeOpened(Event event, Writer writer) throws IOException, ProcessingException {
		try {
			streamWriter.writeStartElement(event.getName());
		} catch (XMLStreamException e) {
			throw new ProcessingException(String.format("There was an error handling the event [%s].", event), e);
		}

	}

	@Override
	protected void handleNodeClosed(Event event, Writer writer) throws IOException, ProcessingException {
		try {
			streamWriter.writeEndElement();
		} catch (XMLStreamException e) {
			throw new ProcessingException(String.format("There was an error handling the event [%s].", event), e);
		}
	}

	@Override
	protected void handleValueAdded(Event event, Writer writer) throws IOException, ProcessingException {
		try {
			streamWriter.writeCharacters(event.getData());
		} catch (XMLStreamException e) {
			throw new ProcessingException(String.format("There was an error handling the event [%s].", event), e);
		}
	}
}
