package com.rocketinsights.core.event.handler;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.rocketinsights.core.event.Event;
import com.rocketinsights.core.event.EventType;
import com.rocketinsights.core.exception.ProcessingException;

/**
 * Translates series of {@link Event}s to a JSON object.
 * 
 * @author fbonecco
 *
 */
public class JsonEventHandler extends DocumentEventHandler {

	private static final String ROOT_NODE = "root";

	private JsonWriter jsonWriter;

	private List<Event> events;

	private Gson gson;

	private JsonArray mainHolder;

	public JsonEventHandler(Writer writer) {
		super(writer);

		init();
	}

	private void init() {
		gson = new GsonBuilder().setPrettyPrinting().create();
		jsonWriter = new JsonWriter(writer);
		jsonWriter.setIndent("  ");
	}

	@Override
	protected void handleDocumentStarted(Event event, Writer writer) throws IOException, ProcessingException {
		jsonWriter.beginObject();
		jsonWriter.name(ROOT_NODE);
		mainHolder = new JsonArray();
		events = new LinkedList<>();
	}

	@Override
	protected void handleDocumentEnded(Event event, Writer writer) throws IOException, ProcessingException {
		gson.toJson(mainHolder, jsonWriter);
		jsonWriter.endObject();
	}

	@Override
	protected void handleNodeOpened(Event event, Writer writer) throws IOException, ProcessingException {
		events.add(event);
	}

	private boolean isEnclosingNode(Event event, List<Event> list) {

		return list.get(0).getName().equals(event.getName())
				&& list.get(list.size() - 1).getName().equals(event.getName());
	}

	private int findNextObjectBound(int from, List<Event> list) {
		Event target = list.get(from);
		for (int x = from + 1; x < list.size(); x++) {
			if (target.getName().equals(list.get(x).getName())) {
				return x;
			}
		}
		return -1;
	}

	private void parseNode(List<Event> subList, JsonElement holder) {
		if (subList.size() > 2) {
			Event event = subList.get(0);
			if (isEnclosingNode(event, subList) && !isValue(subList, event)) {

				if (holder instanceof JsonArray) {
					JsonObject obj = new JsonObject();
					JsonObject newObjHolder = new JsonObject();
					obj.add(event.getName(), newObjHolder);
					((JsonArray) holder).add(obj);
					holder = newObjHolder;
				} else {
					JsonObject auxHolder = new JsonObject();
					((JsonObject) holder).add(event.getName(), auxHolder);
					holder = auxHolder;
				}
				subList = subList.subList(1, subList.size() - 1);
			} else if (isValue(subList, event)) {
				Event value = subList.get(1);
				((JsonObject) holder).addProperty(event.getName(), value.getData());
				subList = subList.subList(3, subList.size());
			}
			parseNode(subList, holder);
		}

	}

	private boolean isValue(List<Event> subList, Event event) {
		boolean isValue = false;
		if (subList.size() >= 3) {
			isValue = event.getEventType().equals(EventType.NODE_OPENED)
					&& subList.get(1).getEventType().equals(EventType.VALUE_ADDED)
					&& subList.get(2).getEventType().equals(EventType.NODE_CLOSED);
		}
		return isValue;
	}

	@Override
	protected void handleNodeClosed(Event event, Writer writer) throws IOException, ProcessingException {
		events.add(event);
		if (isEnclosingNode(event, events)) {
			// once we detect that a closing node
			// denotes the end of a loop,
			// we start building the JSON object and its children
			JsonObject obj = new JsonObject();
			JsonElement holder = new JsonArray();
			obj.add(event.getName(), holder);

			int from = 1;
			int y = findNextObjectBound(from, events);
			while (y > 0 && from < events.size()) {
				parseNode(events.subList(from, y + 1), holder);
				from = y + 1;
				y = findNextObjectBound(from, events);
			}
			mainHolder.add(obj);
			events.clear();
		}
	}

	@Override
	protected void handleValueAdded(Event event, Writer writer) throws IOException, ProcessingException {
		events.add(event);
	}

}
