package com.rocketinsights.core.reader;

import org.apache.commons.io.FilenameUtils;

import com.rocketinsights.core.file.LinesProvider;
import com.rocketinsights.core.file.StreamProvider;

public class ReaderFactory {

	private static final String PROPERTY_EXTENSION = "properties";

	private static final String XML_EXTENSION = "xml";

	private static final String JSON_EXTENSION = "json";

	public Reader createReader(String path) {
		String extension = FilenameUtils.getExtension(path);
		if (PROPERTY_EXTENSION.equals(extension)) {
			return new PropertiesFileReader(new LinesProvider());
		} else if (XML_EXTENSION.equals(extension)) {
			return new XmlFileReader(new StreamProvider());
		} else if (JSON_EXTENSION.equals(extension)) {
			return new JsonFileReader(new StreamProvider());
		}
		throw new IllegalArgumentException(
				String.format("There is no reader for files with extension [%s] within this factory.", extension));

	}

}
