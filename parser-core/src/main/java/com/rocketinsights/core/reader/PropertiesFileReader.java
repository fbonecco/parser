package com.rocketinsights.core.reader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.rocketinsights.core.domain.Property;
import com.rocketinsights.core.event.Event;
import com.rocketinsights.core.event.EventLog;
import com.rocketinsights.core.event.EventType;
import com.rocketinsights.core.exception.InvalidFormatException;
import com.rocketinsights.core.exception.ProcessingException;
import com.rocketinsights.core.file.FileContentProvider;
import com.rocketinsights.core.tree.Node;
import com.rocketinsights.core.tree.PropertiesTree;

/**
 * A reader that can be used to read properties files and to push the
 * corresponding set of {@link Event} to a {@link EventLog} object.
 * 
 * 
 * @author fbonecco
 *
 */
public class PropertiesFileReader implements Reader {

	private FileContentProvider<List<String>> fileContentProvider;

	private PropertiesTree tree;

	public PropertiesFileReader(FileContentProvider<List<String>> fileContentProvider) {
		this.fileContentProvider = fileContentProvider;
	}

	/**
	 * Pulls the data from a file, assuming it contains properties as key,values. If
	 * this method found that the properties are invalids it will end throwing
	 * throws a {@link ProcessingException}. If an IO error occurs, a
	 * {@link IOException} is thrown instead.
	 */
	public void read(Path path, EventLog eventLog) throws IOException, ProcessingException {
		List<String> properties;
		properties = fileContentProvider.readContents(path);

		Property lastProperty = null;
		eventLog.push(new Event(EventType.DOC_STARTED));
		tree = new PropertiesTree();
		tree.setRoot(new Node("root"));

		for (String p : properties) {
			Property currentProperty = null;
			try {
				currentProperty = Property.of(p);
			} catch (InvalidFormatException e) {
				throw new ProcessingException(String.format(
						"An error occured while processing the file [%s] due it has an invalid format.", path), e);
			}

			// check whether the property can be added
			validate(path, currentProperty);

			int count = 0;
			boolean keepMoving = lastProperty != null;
			while (keepMoving) {
				String lastPath = lastProperty.getPath().get(count);
				String currentPath = currentProperty.getPath().get(count);
				boolean areEquals = lastPath.equals(currentPath);
				if (areEquals) {
					count++;
				}
				keepMoving = areEquals && (count < lastProperty.getPath().size())
						&& (count < currentProperty.getPath().size());
			}
			if (count > 0) {
				closeAllNodesUp(lastProperty, lastProperty.getPath().size() - 1, count, eventLog);
			} else if (lastProperty != null) {
				closeAllNodesUp(lastProperty, eventLog);
			}
			processNode(currentProperty, count, eventLog);
			lastProperty = currentProperty;
		}

		closeAllNodesUp(lastProperty, eventLog);

		eventLog.push(new Event(EventType.DOC_ENDED));

	}

	private void validate(Path path, Property currentProperty) throws ProcessingException {
		Node root = new Node(currentProperty.getPath().get(0));
		Node parent = root;
		for (int x = 1; x < currentProperty.getPath().size(); x++) {
			Node node = new Node(currentProperty.getPath().get(x));
			parent.addChild(node);
			parent = node;
		}

		if (!tree.add(root)) {
			throw new ProcessingException(String.format(
					"An error occured while processing the file [%s] due it has an invalid format. The property [%s] collides with some other.",
					path, currentProperty));
		}
	}

	private void closeAllNodesUp(Property lastProperty, int from, int to, EventLog eventBus)
			throws ProcessingException {
		for (int x = from; x >= to; x--) {
			closeNode(lastProperty.getPath().get(x), eventBus);
		}

	}

	private void closeAllNodesUp(Property lastProperty, EventLog eventBus) throws ProcessingException {
		closeAllNodesUp(lastProperty, lastProperty.getPath().size() - 1, 0, eventBus);
	}

	private void closeNode(String name, EventLog eventBus) throws ProcessingException {
		eventBus.push(new Event(EventType.NODE_CLOSED, name, null));
	}

	private void processNode(Property property, int from, EventLog eventBus) throws ProcessingException {
		List<String> path = property.getPath();
		for (String p : path.subList(from, path.size())) {
			eventBus.push(new Event(EventType.NODE_OPENED, p, null));
		}
		eventBus.push(new Event(EventType.VALUE_ADDED, null, property.getValue()));
	}
}
