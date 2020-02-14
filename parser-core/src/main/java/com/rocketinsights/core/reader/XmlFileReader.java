package com.rocketinsights.core.reader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import com.rocketinsights.core.event.Event;
import com.rocketinsights.core.event.EventLog;
import com.rocketinsights.core.event.EventType;
import com.rocketinsights.core.exception.ProcessingException;
import com.rocketinsights.core.file.FileContentProvider;

/**
 * A reader that can be used to read XML files and to push the corresponding set
 * of {@link Event} to a {@link EventLog} object.
 * 
 * 
 * @author fbonecco
 *
 */
public class XmlFileReader implements Reader {
	private static final String VERSION = "1.0";
	private static final String ENCODING = "UTF-8";
	private static final String ROOT_NODE = "root";

	private FileContentProvider<InputStream> fileContentProvider;

	public XmlFileReader(FileContentProvider<InputStream> fileContentProvider) {
		super();
		this.fileContentProvider = fileContentProvider;
	}

	/**
	 * Pulls the data from a file, assuming it contains a XML structure. If the
	 * structure within the file can't be parsed (ie: the xml is malformed) this
	 * method throws a {@link ProcessingException}. If an IO error occurs, a
	 * {@link IOException} is thrown instead.
	 */
	@Override
	public void read(Path path, EventLog eventLog) throws ProcessingException, IOException {
		XMLInputFactory xmlif = XMLInputFactory.newInstance();

		XMLStreamReader reader = null;
		try {
			reader = xmlif.createXMLStreamReader(fileContentProvider.readContents(path));

			initialValidation(reader, path);

			eventLog.push(new Event(EventType.DOC_STARTED, null, null));
			int lastEvent = -1;
			while (reader.hasNext()) {
				int eventType = reader.next();
				switch (eventType) {
				case XMLEvent.START_ELEMENT:
					if (lastEvent == XMLEvent.CHARACTERS) {
						throw new ProcessingException(String.format(
								"An error occured while processing the file [%s] due it has an invalid format.", path));
					}
					eventLog.push(new Event(EventType.NODE_OPENED, reader.getName().toString(), null));
					lastEvent = eventType;
					break;

				case XMLEvent.END_ELEMENT:
					String name = reader.getName().toString();
					if (!ROOT_NODE.equals(name)) {
						eventLog.push(new Event(EventType.NODE_CLOSED, name, null));
					}
					lastEvent = eventType;
					break;

				case XMLEvent.CHARACTERS:
					if (!reader.isWhiteSpace()) {
						eventLog.push(new Event(EventType.VALUE_ADDED, null, reader.getText()));
						lastEvent = eventType;
					} else {
						lastEvent = -1;
					}
					break;
				}
			}
			eventLog.push(new Event(EventType.DOC_ENDED, null, null));
		} catch (XMLStreamException e) {
			throw new ProcessingException(String
					.format("An error occured while processing the file [%s] due it has an invalid format.", path), e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}
	}

	private void initialValidation(XMLStreamReader reader, Path path) throws XMLStreamException, ProcessingException {
		if (!VERSION.equals(reader.getVersion()) || !ENCODING.equals(reader.getEncoding())) {
			throw new ProcessingException(String.format(
					"The format of the file %s is invalid and does not match the specification. Check whether version [%s] and encoding [%s] attributes are correct.",
					path, reader.getVersion(), reader.getEncoding()));
		}

		if (reader.hasNext()) {
			int eventType = reader.next();
			if (XMLEvent.START_ELEMENT == eventType && "root".equals(reader.getName().toString())) {
				return;
			}
		}
		throw new ProcessingException(
				String.format("The format of the file %s is invalid and does not match the specification.", path));

	}
}
