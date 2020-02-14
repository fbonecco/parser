package com.rocketinsights.core.reader;

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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.stream.MalformedJsonException;
import com.rocketinsights.core.event.Event;
import com.rocketinsights.core.event.EventLog;
import com.rocketinsights.core.event.EventType;
import com.rocketinsights.core.exception.ProcessingException;
import com.rocketinsights.core.file.FileContentProvider;

@RunWith(MockitoJUnitRunner.class)
public class JsonFileReaderTest {

	private static final String SAMPLE_JSON_1 = "json/sample1.json";
	private static final String SAMPLE_JSON_2 = "json/sample2.json";
	private static final String SAMPLE_JSON_3 = "json/sample3.json";
	private static final String WRONG_FILE = "/path/to/file";
	private static final String MALFORMED_JSON = "json/malformed.json";
	private static final String INVALID_JSON_FORMAT = "json/invalid-format.json";

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	private JsonFileReader underTest;

	@Mock
	private FileContentProvider<InputStream> fileContentProvider;

	@Mock
	private EventLog eventLog;

	@Before
	public void setUp() {
		underTest = new JsonFileReader(fileContentProvider);
	}

	@Test
	public void testRead_notExistingFile() throws ProcessingException, IOException {
		exceptionRule.expect(IOException.class);

		Path path = Paths.get(WRONG_FILE);
		doThrow(new IOException()).when(fileContentProvider).readContents(path);

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
	public void testRead_malformedJson() throws IOException, ProcessingException {
		exceptionRule.expect(ProcessingException.class);
		exceptionRule.expectCause(isA(IOException.class));

		Path path = Paths.get(MALFORMED_JSON);
		when(fileContentProvider.readContents(path)).thenReturn(getStream(MALFORMED_JSON));

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
	public void testRead_invalidJsonFormat() throws IOException, ProcessingException {
		exceptionRule.expect(ProcessingException.class);
		exceptionRule.expectCause(isA(MalformedJsonException.class));

		Path path = Paths.get(INVALID_JSON_FORMAT);
		when(fileContentProvider.readContents(path)).thenReturn(getStream(INVALID_JSON_FORMAT));

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
		Path path = Paths.get(SAMPLE_JSON_1);
		when(fileContentProvider.readContents(path)).thenReturn(getStream(SAMPLE_JSON_1));

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
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "carriers", null));
		inOrder.verify(eventLog).push(new Event(EventType.DOC_ENDED));
	}

	@Test
	public void testRead_validFile2() throws IOException, ProcessingException {
		Path path = Paths.get(SAMPLE_JSON_2);
		when(fileContentProvider.readContents(path)).thenReturn(getStream(SAMPLE_JSON_2));

		underTest.read(path, eventLog);

		verify(fileContentProvider, times(1)).readContents(path);

		InOrder inOrder = inOrder(eventLog);

		inOrder.verify(eventLog).push(new Event(EventType.DOC_STARTED));
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
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "Estados Unidos 465"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "address", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "location", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "brigada", null));

		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "viejoalmacen", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "name", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "El Viejo Almacén"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "name", null));
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
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "phone", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "011 4307-7388"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "phone", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "location", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "viejoalmacen", null));

		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "restaurants", null));
		inOrder.verify(eventLog).push(new Event(EventType.DOC_ENDED));
	}

	@Test
	public void testRead_validFile3() throws IOException, ProcessingException {
		Path path = Paths.get(SAMPLE_JSON_3);
		when(fileContentProvider.readContents(path)).thenReturn(getStream(SAMPLE_JSON_3));

		underTest.read(path, eventLog);

		verify(fileContentProvider, times(1)).readContents(path);

		InOrder inOrder = inOrder(eventLog);

		inOrder.verify(eventLog).push(new Event(EventType.DOC_STARTED));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "restaurants", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "brigada", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "location", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "address", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "Estados Unidos 465"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "address", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "location", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "brigada", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "restaurants", null));

		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "carriers", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "personal", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_OPENED, "name", null));
		inOrder.verify(eventLog).push(new Event(EventType.VALUE_ADDED, null, "Telecom Personal"));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "name", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "personal", null));
		inOrder.verify(eventLog).push(new Event(EventType.NODE_CLOSED, "carriers", null));

		inOrder.verify(eventLog).push(new Event(EventType.DOC_ENDED));
	}

	private InputStream getStream(String path) {
		return getClass().getClassLoader().getResourceAsStream(path);
	}

}
