package com.rocketinsights.core.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class LinesProvider implements FileContentProvider<List<String>> {

	private static final String ENCODING = "UTF-8";

	@Override
	public List<String> readContents(Path path) throws IOException {
		File file = path.toFile();
		List<String> lines = new LinkedList<>();
		try (LineIterator iterator = FileUtils.lineIterator(file, ENCODING)) {
			iterator.forEachRemaining(l -> {
				if (!l.trim().isEmpty()) {
					lines.add(l);
				}
			});
		}
		return lines;
	}
}
