package com.rocketinsights.core.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Stack;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import com.rocketinsights.core.event.Event;
import com.rocketinsights.core.event.EventLog;
import com.rocketinsights.core.event.EventType;
import com.rocketinsights.core.exception.ProcessingException;
import com.rocketinsights.core.file.FileContentProvider;

/**
 * A reader that can be used to read JSON files and to push the corresponding
 * set of {@link Event} to a {@link EventLog} object.
 * 
 * 
 * @author fbonecco
 *
 */
public class JsonFileReader implements Reader {
	private static final String ROOT_NODE = "root";
	private FileContentProvider<InputStream> fileContentProvider;

	public JsonFileReader(FileContentProvider<InputStream> fileContentProvider) {
		super();
		this.fileContentProvider = fileContentProvider;
	}

	/**
	 * Pulls the data from a file, assuming it contains a JSON object. If the
	 * structure within the file can't be parsed to a JSON object this method throws
	 * a {@link ProcessingException}. If an IO error occurs, a {@link IOException}
	 * is thrown instead.
	 */
	@Override
	public void read(Path path, EventLog eventLog) throws IOException, ProcessingException {
		try (JsonReader reader = new JsonReader(new InputStreamReader(fileContentProvider.readContents(path)))) {

			initialValidation(reader, path);

			eventLog.push(new Event(EventType.DOC_STARTED, null, null));

			Stack<String> nodes = new Stack<>();
			while (!reader.peek().equals(JsonToken.END_DOCUMENT)) {
				JsonToken next = reader.peek();
				switch (next) {
				case BEGIN_OBJECT:
					reader.beginObject();
					break;
				case NAME:
					String name = reader.nextName();
					nodes.push(reader.getPath());
					eventLog.push(new Event(EventType.NODE_OPENED, name, null));
					break;
				case STRING:
					eventLog.push(new Event(EventType.VALUE_ADDED, null, reader.nextString()));
					closeNode(eventLog, reader, nodes);
					break;
				case END_OBJECT:
					closeNode(eventLog, reader, nodes);
					reader.endObject();
					break;
				case BEGIN_ARRAY:
					reader.beginArray();
					break;
				case END_ARRAY:
					reader.endArray();
					break;
				default:
					break;
				}
			}

			eventLog.push(new Event(EventType.DOC_ENDED, null, null));
		} catch (MalformedJsonException e) {
			throw new ProcessingException(String
					.format("An error occured while processing the file [%s] due it has an invalid format.", path), e);
		}
	}

	private void initialValidation(JsonReader reader, Path path) throws IOException, ProcessingException {
		reader.beginObject();
		if (!ROOT_NODE.equals(reader.nextName())) {
			throw new MalformedJsonException(String
					.format("The format of the file [%s] is invalid and does not match the specification.", path));
		}
	}

	private void closeNode(EventLog eventBus, JsonReader reader, Stack<String> nodes) throws ProcessingException {
		if (!nodes.isEmpty() && reader.getPath().equals(nodes.peek())) {
			eventBus.push(new Event(EventType.NODE_CLOSED, getName(nodes.pop()), null));
		}
	}

	private String getName(String pop) {
		return pop.substring(pop.lastIndexOf(".") + 1);
	}

}
