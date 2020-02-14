package com.rocketinsights.core.reader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.rocketinsights.core.event.Event;
import com.rocketinsights.core.event.EventLog;
import com.rocketinsights.core.event.EventType;
import com.rocketinsights.core.exception.ProcessingException;
import com.rocketinsights.core.file.FileContentProvider;

@RunWith(MockitoJUnitRunner.class)
public class XmlReaderTest {

	private static final String SAMPLE_XML_1 = "xml/sample1.xml";
	private static final String SAMPLE_XML_2 = "xml/sample2.xml";
	private static final String WRONG_FILE_PATH = "/path/to/file";
	private static final String MALFORMED_XML_1 = "xml/malformed1.xml";
	private static final String MALFORMED_XML_2 = "xml/malformed2.xml";
	private static final String WRONG_VERSION_XML = "xml/wrong-version.xml";
	private static final String WRONG_ENCODING_XML = "xml/wrong-encoding.xml";
	private static final String WRONG_FORMAT_XML = "xml/wrong-format.xml";
	private static final String MISSING_HEADER_XML = "xml/missing-header.xml";

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	private XmlFileReader underTest;

	@Mock
	private FileContentProvider<InputStream> fileContentProvider;

	@Mock
	private EventLog eventLog;

	@Before
	public void setUp() {
		underTest = new XmlFileReader(fileContentProvider);
	}

	@Test
	public void testRead_notExistingFile() throws IOException, ProcessingException {
		exceptionRule.expect(IOException.class);

		Path path = Paths.get(WRONG_FILE_PATH);
		doThrow(new IOException()).when(fileContentProvider).readContents(path);

		IOException ex = null;
		try {
			underTest.read(path, eventLog);
		} catch (IOException e) {
			ex = e;
		}
		verify(fileContentProvider, times(1)).readContents(path);
		throw ex;
	}

	@Test
	public void testRead_malformedXml1() throws IOException, ProcessingException {
		exceptionRule.expect(ProcessingException.class);
		exceptionRule.expectCause(isA(XMLStreamException.class));

		Path path = Paths.get(MALFORMED_XML_1);
		when(fileContentProvider.readContents(path)).thenReturn(getStream(MALFORMED_XML_1));

		ProcessingException ex = null;
		try {
			underTest.read(path, eventLog);
		} catch (ProcessingException e) {
			ex = e;
		}
		verify(fileContentProvider, times(1)).readContents(path);
		throw ex;
	}

	@Test
	public void testRead_malformedXml2() throws IOException, ProcessingException {
		exceptionRule.expect(ProcessingException.class);

		Path path = Paths.get(MALFORMED_XML_2);
		when(fileContentProvider.readContents(path)).thenReturn(getStream(MALFORMED_XML_2));

		ProcessingException ex = null;
		try {
			underTest.read(path, eventLog);
		} catch (ProcessingException e) {
			ex = e;
		}
		verify(fileContentProvider, times(1)).readContents(path);
		throw ex;
	}

	@Test
	public void testRead_wrongVersion() throws IOException, ProcessingException {
		exceptionRule.expect(ProcessingException.class);
		exceptionRule.expectCause(isA(XMLStreamException.class));

		Path path = Paths.get(WRONG_VERSION_XML);
		when(fileContentProvider.readContents(path)).thenReturn(getStream(WRONG_VERSION_XML));

		ProcessingException ex = null;
		try {
			underTest.read(path, eventLog);
		} catch (ProcessingException e) {
			ex = e;
		}
		verify(fileContentProvider, times(1)).readContents(path);
		throw ex;
	}

	@Test
	public void testRead_wrongEncoding() throws IOException, ProcessingException {
		exceptionRule.expect(ProcessingException.class);
		exceptionRule.expectCause(isA(XMLStreamException.class));

		Path path = Paths.get(WRONG_ENCODING_XML);
		when(fileContentProvider.readContents(path)).thenReturn(getStream(WRONG_ENCODING_XML));

		ProcessingException ex = null;
		try {
			underTest.read(path, eventLog);
		} catch (ProcessingException e) {
			ex = e;
		}
		verify(fileContentProvider, times(1)).readContents(path);
		throw ex;
	}

	@Test
	public void testRead_missingHeader() throws IOException, ProcessingException {
		exceptionRule.expect(ProcessingException.class);
		Throwable expected = null;
		exceptionRule.expectCause(is(expected));

		Path path = Paths.get(MISSING_HEADER_XML);
		when(fileContentProvider.readContents(path)).thenReturn(getStream(MISSING_HEADER_XML));

		ProcessingException ex = null;
		try {
			underTest.read(path, eventLog);
		} catch (ProcessingException e) {
			ex = e;
		}
		verify(fileContentProvider, times(1)).readContents(path);
		throw ex;
	}

	@Test
	public void testRead_wrongFormat() throws IOException, ProcessingException {
		exceptionRule.expect(ProcessingException.class);
		Throwable cause = null;
		exceptionRule.expectCause(is(cause));

		Path path = Paths.get(WRONG_FORMAT_XML);
		when(fileContentProvider.readContents(path)).thenReturn(getStream(WRONG_FORMAT_XML));

		ProcessingException ex = null;
		try {
			underTest.read(path, eventLog);
		} catch (ProcessingException e) {
			ex = e;
		}
		verify(fileContentProvider, times(1)).readContents(path);
		throw ex;
	}

	@Test
	public void testRead_validFile1() throws IOException, ProcessingException {
		Path path = Paths.get(SAMPLE_XML_1);
		when(fileContentProvider.readContents(path)).thenReturn(getStream(SAMPLE_XML_1));

		underTest.read(path, eventLog);

		verify(fileContentProvider, times(1)).readContents(path);

		InOrder inOrder = inOrder(eventLog);

		inOrder.verify(eventLog).push(new Event(EventType.DOC_STARTED));

		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "carriers", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "personal", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "name", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "Telecom Personal"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "name", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "country", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "Argentina"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "country", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "address", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "Av. Corrientes 566"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "address", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "personal", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "claro", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "name", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "Claro Argentina"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "name", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "country", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "Argentina"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "country", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "address", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "Av. Paseo Colón 505"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "address", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "claro", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "carriers", null));

		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "restaurants", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "brigada", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "name", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "La Brigada"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "name", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "location", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "country", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "Argentina"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "country", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "neighborhood", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "San Telmo"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "neighborhood", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "address", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "Av. Paseo Colón"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "address", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "location", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "brigada", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "viejoalmacen", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "name", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "El Viejo Almacén"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "name", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "phone", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "011 4307-7388"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "phone", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "location", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "country", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "Argentina"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "country", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "neighborhood", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "San Telmo"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "neighborhood", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "address", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "Av. Independencia 299"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "address", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "location", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "viejoalmacen", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "restaurants", null));

		inOrder.verify(eventLog).push(new Event(EventType.DOC_ENDED));
	}

	@Test
	public void testRead_validFile2() throws IOException, ProcessingException {
		Path path = Paths.get(SAMPLE_XML_2);
		when(fileContentProvider.readContents(path)).thenReturn(getStream(SAMPLE_XML_2));

		underTest.read(path, eventLog);

		verify(fileContentProvider, times(1)).readContents(path);

		InOrder inOrder = inOrder(eventLog);

		inOrder.verify(eventLog).push(new Event(EventType.DOC_STARTED));

		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "carriers", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "personal", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "personal", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "carriers", null));

		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "restaurants", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "brigada", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "name", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "La Brigada"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "name", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "brigada", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "restaurants", null));

		inOrder.verify(eventLog).push(new Event(EventType.DOC_ENDED));
	}

	private InputStream getStream(String path) {
		return getClass().getClassLoader().getResourceAsStream(path);
	}
}
