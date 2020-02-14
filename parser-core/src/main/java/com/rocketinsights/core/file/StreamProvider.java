package com.rocketinsights.core.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

public class StreamProvider implements FileContentProvider<InputStream> {

	@Override
	public InputStream readContents(Path path) throws IOException {
		File file = path.toFile();
		return FileUtils.openInputStream(file);
	}

}
