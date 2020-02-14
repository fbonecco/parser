package com.rocketinsights.core.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.rocketinsights.core.exception.ProcessingException;
import com.rocketinsights.core.parser.DefaultParser;
import com.rocketinsights.core.parser.SupportedFormat;

public class DefaultParserTest {
	private static final String CLEANUP_REGEX = "[\r\n\t]";
	private static final String SAMPLE_PROPERTIES_1 = "properties/sample1.properties";
	private static final String SAMPLE_XML_1 = "xml/sample1.xml";
	private static final String SAMPLE_JSON_1 = "json/sample1.json";

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private DefaultParser underTest;

	@Before
	public void setUp() {
		underTest = new DefaultParser();
	}

	// Tests properties conversion
	@Test
	public void test_parsePropertyToXmlBackAndForth() throws IOException, ProcessingException, URISyntaxException {
		Path path = getFilePath(SAMPLE_PROPERTIES_1);
		File outputFile = folder.newFile("output.xml");

		assertThat(outputFile.length(), is(equalTo(0L)));

		// first convert .properties to .xml
		underTest.parse(path.toString(), outputFile.toPath().toString(), SupportedFormat.XML);
		// then convert the .xml back to .properties
		File outputFile2 = folder.newFile("output.properties");
		underTest.parse(outputFile.toPath().toString(), outputFile2.toPath().toString(), SupportedFormat.PROPERTY);

		// use Java's Properties classes to compare values
		Properties sourceProperties = new Properties();
		sourceProperties.load(Files.newInputStream(path, StandardOpenOption.READ));

		Properties convertedProperties = new Properties();
		convertedProperties.load(Files.newInputStream(outputFile2.toPath(), StandardOpenOption.READ));

		assertThat(sourceProperties, is(equalTo(convertedProperties)));
	}

	@Test
	public void test_parsePropertyToJsonBackAndForth() throws IOException, ProcessingException, URISyntaxException {
		Path path = getFilePath(SAMPLE_PROPERTIES_1);
		File outputFile = folder.newFile("output.json");

		assertThat(outputFile.length(), is(equalTo(0L)));

		// first convert .properties to .json
		underTest.parse(path.toString(), outputFile.toPath().toString(), SupportedFormat.JSON);
		// then convert the .json back to .properties
		File outputFile2 = folder.newFile("output.properties");
		underTest.parse(outputFile.toPath().toString(), outputFile2.toPath().toString(), SupportedFormat.PROPERTY);

		// use Java's Properties classes to compare values
		Properties sourceProperties = new Properties();
		sourceProperties.load(Files.newInputStream(path, StandardOpenOption.READ));

		Properties convertedProperties = new Properties();
		convertedProperties.load(Files.newInputStream(outputFile2.toPath(), StandardOpenOption.READ));

		assertThat(sourceProperties, is(equalTo(convertedProperties)));
	}

	@Test
	public void test_parsePropertyToXml() throws IOException, ProcessingException, URISyntaxException {
		Path path = getFilePath(SAMPLE_PROPERTIES_1);
		File outputFile = folder.newFile("output.xml");

		assertThat(outputFile.length(), is(equalTo(0L)));

		underTest.parse(path.toString(), outputFile.toPath().toString(), SupportedFormat.XML);

		assertThat(outputFile.length(), is(not(equalTo(0))));
	}

	@Test
	public void test_parsePropertyToJson() throws IOException, ProcessingException, URISyntaxException {
		Path path = getFilePath(SAMPLE_PROPERTIES_1);
		File outputFile = folder.newFile("output.json");

		assertThat(outputFile.length(), is(equalTo(0L)));

		underTest.parse(path.toString(), outputFile.toPath().toString(), SupportedFormat.JSON);

		assertThat(outputFile.length(), is(not(equalTo(0))));
	}

	// Tests for xml conversion
	@Test
	public void test_parseXmlToPropertyBackAndForth() throws IOException, ProcessingException, URISyntaxException {
		Path path = getFilePath(SAMPLE_XML_1);
		File outputFile = folder.newFile("output.properties");

		assertThat(outputFile.length(), is(equalTo(0L)));

		// first convert .xml to .properties
		underTest.parse(path.toString(), outputFile.toPath().toString(), SupportedFormat.PROPERTY);
		// then convert the .properties back to .xml
		File outputFile2 = folder.newFile("output.xml");
		underTest.parse(outputFile.toPath().toString(), outputFile2.toPath().toString(), SupportedFormat.XML);

		// compare xml in Strings
		String sourceProperties = new String(Files.readAllBytes(path)).replaceAll(CLEANUP_REGEX, "");
		String convertedProperties = new String(Files.readAllBytes(outputFile2.toPath())).replaceAll(CLEANUP_REGEX, "");

		assertThat(sourceProperties, is(equalTo(convertedProperties)));
	}

	@Test
	public void test_parseXmlToJsonBackAndForth() throws IOException, ProcessingException, URISyntaxException {
		Path path = getFilePath(SAMPLE_XML_1);
		File outputFile = folder.newFile("output.json");

		assertThat(outputFile.length(), is(equalTo(0L)));

		// first convert .xml to .json
		underTest.parse(path.toString(), outputFile.toPath().toString(), SupportedFormat.JSON);
		// then convert the .json back to .xml
		File outputFile2 = folder.newFile("output.xml");
		underTest.parse(outputFile.toPath().toString(), outputFile2.toPath().toString(), SupportedFormat.XML);

		// compare xml in Strings
		String sourceProperties = new String(Files.readAllBytes(path)).replaceAll(CLEANUP_REGEX, "");
		String convertedProperties = new String(Files.readAllBytes(outputFile2.toPath())).replaceAll(CLEANUP_REGEX, "");

		assertThat(sourceProperties, is(equalTo(convertedProperties)));
	}

	@Test
	public void test_parseXmlToProperty() throws IOException, ProcessingException, URISyntaxException {
		Path path = getFilePath(SAMPLE_XML_1);
		File outputFile = folder.newFile("output.properties");

		assertThat(outputFile.length(), is(equalTo(0L)));

		underTest.parse(path.toString(), outputFile.toPath().toString(), SupportedFormat.PROPERTY);

		assertThat(outputFile.length(), is(not(equalTo(0))));
	}

	@Test
	public void test_parseXmlToJson() throws IOException, ProcessingException, URISyntaxException {
		Path path = getFilePath(SAMPLE_XML_1);
		File outputFile = folder.newFile("output.json");

		assertThat(outputFile.length(), is(equalTo(0L)));

		underTest.parse(path.toString(), outputFile.toPath().toString(), SupportedFormat.JSON);

		assertThat(outputFile.length(), is(not(equalTo(0))));
	}

	// Tests for json conversion
	@Test
	public void test_parseJsonToPropertyBackAndForth() throws IOException, ProcessingException, URISyntaxException {
		Path path = getFilePath(SAMPLE_JSON_1);
		File outputFile = folder.newFile("output.properties");

		assertThat(outputFile.length(), is(equalTo(0L)));

		// first convert .json to .properties
		underTest.parse(path.toString(), outputFile.toPath().toString(), SupportedFormat.PROPERTY);
		// then convert the .properties back to .json
		File outputFile2 = folder.newFile("output.json");
		underTest.parse(outputFile.toPath().toString(), outputFile2.toPath().toString(), SupportedFormat.JSON);

		// compare json using Gson
		String sourceProperties = new String(Files.readAllBytes(path)).replaceAll(CLEANUP_REGEX, "");
		String convertedProperties = new String(Files.readAllBytes(outputFile2.toPath())).replaceAll(CLEANUP_REGEX, "");

		JsonElement p1 = JsonParser.parseString(sourceProperties);
		JsonElement p2 = JsonParser.parseString(convertedProperties);
		assertThat(p1, is(equalTo(p2)));
	}

	@Test
	public void test_parseJsonToXmlBackAndForth() throws IOException, ProcessingException, URISyntaxException {
		Path path = getFilePath(SAMPLE_JSON_1);
		File outputFile = folder.newFile("output.xml");

		assertThat(outputFile.length(), is(equalTo(0L)));

		// first convert .json to .xml
		underTest.parse(path.toString(), outputFile.toPath().toString(), SupportedFormat.XML);
		// then convert the .xml back to .json
		File outputFile2 = folder.newFile("output.json");
		underTest.parse(outputFile.toPath().toString(), outputFile2.toPath().toString(), SupportedFormat.JSON);

		// compare json using Gson
		String sourceProperties = new String(Files.readAllBytes(path)).replaceAll(CLEANUP_REGEX, "");
		String convertedProperties = new String(Files.readAllBytes(outputFile2.toPath())).replaceAll(CLEANUP_REGEX, "");
		JsonElement p1 = JsonParser.parseString(sourceProperties);
		JsonElement p2 = JsonParser.parseString(convertedProperties);
		assertThat(p1, is(equalTo(p2)));
	}

	@Test
	public void test_parseJsonToProperty() throws IOException, ProcessingException, URISyntaxException {
		Path path = getFilePath(SAMPLE_JSON_1);
		File outputFile = folder.newFile("output.properties");

		assertThat(outputFile.length(), is(equalTo(0L)));

		underTest.parse(path.toString(), outputFile.toPath().toString(), SupportedFormat.PROPERTY);

		assertThat(outputFile.length(), is(not(equalTo(0))));
	}

	@Test
	public void test_parseJsonToXml() throws IOException, ProcessingException, URISyntaxException {
		Path path = getFilePath(SAMPLE_JSON_1);
		File outputFile = folder.newFile("output.xml");

		assertThat(outputFile.length(), is(equalTo(0L)));

		underTest.parse(path.toString(), outputFile.toPath().toString(), SupportedFormat.XML);

		assertThat(outputFile.length(), is(not(equalTo(0))));
	}

	private Path getFilePath(String path) throws IOException, URISyntaxException {
		return Paths.get(this.getClass().getClassLoader().getResource(path).toURI());

	}
}
