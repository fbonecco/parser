package com.rocketinsights.core.file;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class StreamProviderTest {

	private static final String SAMPLE_FILE_PATH_1 = "properties/sample1.properties";

	private FileContentProvider<InputStream> underTest;

	@Before
	public void setUp() {
		underTest = new StreamProvider();
	}

	@Test
	public void test_validFile() throws IOException, URISyntaxException {
		InputStream is = underTest
				.readContents(Paths.get(getClass().getClassLoader().getResource(SAMPLE_FILE_PATH_1).toURI()));
		assertThat(is, is(notNullValue()));
	}

	@Test(expected = IOException.class)
	public void test_notExistingFile() throws IOException {
		underTest.readContents(Paths.get("/path/to/not/existing/file"));
	}

}
