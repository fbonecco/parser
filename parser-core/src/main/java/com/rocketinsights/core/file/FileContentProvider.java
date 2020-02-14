package com.rocketinsights.core.file;

import java.io.IOException;
import java.nio.file.Path;

public interface FileContentProvider<T> {

	public T readContents(Path path) throws IOException;
}
