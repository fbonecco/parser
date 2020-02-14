package com.rocketinsights.core.file;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class LineContentProviderTest {

	private FileContentProvider<List<String>> underTest;

	@Before
	public void setUp() {
		underTest = new LinesProvider();
	}

	@Test(expected = IOException.class)
	public void test_notExistingFile() throws IOException {
		underTest.readContents(Paths.get("/path/to/not/existing/file"));
	}
}
