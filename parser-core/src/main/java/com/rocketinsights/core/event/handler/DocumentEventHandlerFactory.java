package com.rocketinsights.core.event.handler;

import java.io.IOException;
import java.io.Writer;

import com.rocketinsights.core.parser.SupportedFormat;

public class DocumentEventHandlerFactory implements EventHandlerFactory {

	@Override
	public DocumentEventHandler createEventHandler(SupportedFormat format, Writer writer) throws IOException {
		if (writer == null) {
			throw new IllegalArgumentException("Writter can be null.");
		}
		if (SupportedFormat.XML.equals(format)) {
			return new XmlEventHandler(writer);
		} else if (SupportedFormat.PROPERTY.equals(format)) {
			return new PropertyEventHandler(writer);
		} else if (SupportedFormat.JSON.equals(format)) {
			return new JsonEventHandler(writer);
		}
		throw new IllegalArgumentException(String.format("The format [%s] is not supported by this factory.", format));
	}

}
