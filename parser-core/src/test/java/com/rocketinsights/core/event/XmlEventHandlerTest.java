package com.rocketinsights.core.event;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.rocketinsights.core.event.handler.XmlEventHandler;
import com.rocketinsights.core.exception.ProcessingException;

public class XmlEventHandlerTest {

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	private static final String CLEANUP_REGEX = "[\r\n\t]";
	private static final String SAMPLE_XML_1 = "xml/sample1.xml";
	private static final String SAMPLE_XML_2 = "xml/sample2.xml";

	private XmlEventHandler underTest;

	@Test
	public void testRead_ioError() throws IOException, ProcessingException {
		exceptionRule.expect(ProcessingException.class);
		exceptionRule.expectCause(isA(XMLStreamException.class));

		ProcessingException ex = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (Writer writer = new OutputStreamWriter(out, "UTF-8")) {
			underTest = new XmlEventHandler(writer);

			writer.close();

			underTest.handle(new Event(EventType.DOC_STARTED));
			underTest.handle(new Event(EventType.NODE_OPENED, "carriers", null));

		} catch (ProcessingException e) {
			ex = e;
		}
		throw ex;
	}

	@Test
	public void test_handleXml1() throws UnsupportedEncodingException, IOException, ProcessingException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String path = SAMPLE_XML_1;
		try (Writer writer = new OutputStreamWriter(out, "UTF-8")) {
			underTest = new XmlEventHandler(writer);

			underTest.handle(new Event(EventType.DOC_STARTED));

			underTest.handle(new Event(EventType.NODE_OPENED, "carriers", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "personal", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "name", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "Telecom Personal"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "name", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "country", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "Argentina"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "country", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "address", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "Av. Corrientes 566"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "address", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "personal", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "claro", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "name", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "Claro Argentina"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "name", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "country", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "Argentina"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "country", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "address", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "Av. Paseo Colón 505"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "address", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "claro", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "carriers", null));

			underTest.handle(new Event(EventType.NODE_OPENED, "restaurants", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "brigada", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "name", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "La Brigada"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "name", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "location", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "country", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "Argentina"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "country", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "neighborhood", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "San Telmo"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "neighborhood", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "address", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "Av. Paseo Colón"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "address", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "location", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "brigada", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "viejoalmacen", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "name", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "El Viejo Almacén"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "name", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "phone", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "011 4307-7388"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "phone", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "location", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "country", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "Argentina"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "country", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "neighborhood", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "San Telmo"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "neighborhood", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "address", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "Av. Independencia 299"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "address", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "location", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "viejoalmacen", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "restaurants", null));

			underTest.handle(new Event(EventType.DOC_ENDED));

			String expected = getFileContents(path).replaceAll(CLEANUP_REGEX, "");
			writer.flush();
			String actual = new String(out.toByteArray()).replaceAll(CLEANUP_REGEX, "");
			assertThat(actual, is(equalTo(expected)));
		}
	}

	@Test
	public void test_handleXml2() throws IOException, ProcessingException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String path = SAMPLE_XML_2;
		try (Writer writer = new OutputStreamWriter(out, "UTF-8")) {
			underTest = new XmlEventHandler(writer);
			underTest.handle(new Event(EventType.DOC_STARTED));

			underTest.handle(new Event(EventType.NODE_OPENED, "carriers", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "personal", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "personal", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "carriers", null));

			underTest.handle(new Event(EventType.NODE_OPENED, "restaurants", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "brigada", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "name", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "La Brigada"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "name", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "brigada", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "restaurants", null));

			underTest.handle(new Event(EventType.DOC_ENDED));

			String expected = getFileContents(path).replaceAll(CLEANUP_REGEX, "");
			writer.flush();
			String actual = new String(out.toByteArray()).replaceAll(CLEANUP_REGEX, "");
			assertThat(actual, is(equalTo(expected)));
		}
	}

	private String getFileContents(String path) throws IOException {
		return FileUtils.readFileToString(FileUtils.toFile(getClass().getClassLoader().getResource(path)), "UTF-8");
	}

}
