package com.rocketinsights.core.reader;

import static org.hamcrest.CoreMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
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
import com.rocketinsights.core.exception.InvalidFormatException;
import com.rocketinsights.core.exception.ProcessingException;
import com.rocketinsights.core.file.FileContentProvider;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesFileReaderTest {

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	private static final String SAMPLE_PROPERTIES_1 = "properties/sample1.properties";
	private static final String SAMPLE_PROPERTIES_2 = "properties/sample2.properties";
	private static final String SAMPLE_PROPERTIES_3 = "properties/sample3.properties";
	private static final String INVALID_PROPERTIES_1 = "properties/invalid1.properties";
	private static final String INVALID_PROPERTIES_2 = "properties/invalid2.properties";
	private static final String INVALID_PROPERTIES_3 = "properties/invalid3.properties";
	private static final String WRONG_FILE = "/path/to/file";

	private PropertiesFileReader underTest;

	@Mock
	private EventLog eventLog;

	@Mock
	private FileContentProvider<List<String>> fileContentProvider;

	@Before
	public void setUp() {
		underTest = new PropertiesFileReader(fileContentProvider);
	}

	@Test
	public void testRead_notExistingFile() throws IOException, ProcessingException {
		exceptionRule.expect(IOException.class);

		Path path = Paths.get(WRONG_FILE);
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

	@Test()
	public void testRead_emptyFile() throws IOException, ProcessingException {
		exceptionRule.expect(ProcessingException.class);
		exceptionRule.expectCause(isA(InvalidFormatException.class));

		Path path = Paths.get("/path/to/file");
		when(fileContentProvider.readContents(path)).thenReturn(Arrays.asList(new String[] { "" }));

		ProcessingException ex = null;
		try {
			underTest.read(path, eventLog);
		} catch (ProcessingException e) {
			ex = e;
		}
		verify(fileContentProvider, times(1)).readContents(path);
		throw ex;
	}

	@Test()
	public void testRead_invalidPropertyFormat() throws IOException, ProcessingException {
		exceptionRule.expect(ProcessingException.class);
		exceptionRule.expectCause(isA(InvalidFormatException.class));

		Path path = Paths.get("/path/to/file");
		when(fileContentProvider.readContents(path)).thenReturn(Arrays.asList(new String[] { "" }));

		ProcessingException ex = null;
		try {
			underTest.read(path, eventLog);
		} catch (ProcessingException e) {
			ex = e;
		}
		verify(fileContentProvider, times(1)).readContents(path);
		throw ex;
	}

	@Test()
	public void testRead_invalidPropertyFile1() throws IOException, ProcessingException {
		exceptionRule.expect(ProcessingException.class);

		Path path = Paths.get(INVALID_PROPERTIES_1);
		when(fileContentProvider.readContents(path)).thenReturn(getFileContents(INVALID_PROPERTIES_1));

		ProcessingException ex = null;
		try {
			underTest.read(path, eventLog);
		} catch (ProcessingException e) {
			ex = e;
		}
		verify(fileContentProvider, times(1)).readContents(path);
		throw ex;
	}

	@Test()
	public void testRead_invalidPropertyFile2() throws IOException, ProcessingException {
		exceptionRule.expect(ProcessingException.class);

		Path path = Paths.get(INVALID_PROPERTIES_2);
		when(fileContentProvider.readContents(path)).thenReturn(getFileContents(INVALID_PROPERTIES_2));

		ProcessingException ex = null;
		try {
			underTest.read(path, eventLog);
		} catch (ProcessingException e) {
			ex = e;
		}
		verify(fileContentProvider, times(1)).readContents(path);
		throw ex;
	}

	@Test()
	public void testRead_invalidPropertyFile3() throws IOException, ProcessingException {
		exceptionRule.expect(ProcessingException.class);

		Path path = Paths.get(INVALID_PROPERTIES_3);
		when(fileContentProvider.readContents(path)).thenReturn(getFileContents(INVALID_PROPERTIES_3));

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
		// carriers.personal.name = "Telecom Personal"
		// carriers.personal.country = "Argentina"
		// carriers.personal.address = "Av. Corrientes 566"
		Path path = Paths.get(SAMPLE_PROPERTIES_1);
		when(fileContentProvider.readContents(path)).thenReturn(getFileContents(SAMPLE_PROPERTIES_1));

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
		// restaurants.brigada.name = "La Brigada"
		// restaurants.brigada.location.country = "Argentina"
		// restaurants.brigada.location.neighborhood = "San Telmo"
		// restaurants.brigada.location.address = "Estados Unidos 465"

		// restaurants.viejoalmacen.name = "El Viejo Almacén"
		// restaurants.viejoalmacen.phone = "011 4307-7388"
		// restaurants.viejoalmacen.location.country = "Argentina"
		// restaurants.viejoalmacen.location.neighborhood = "San Telmo"
		// restaurants.viejoalmacen.location.address = "Av. Independencia 299"
		Path path = Paths.get(SAMPLE_PROPERTIES_2);
		when(fileContentProvider.readContents(path)).thenReturn(getFileContents(SAMPLE_PROPERTIES_2));

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
	public void testRead_validFile3() throws IOException, ProcessingException {
		// restaurants.brigada.location.address = "Estados Unidos 465"
		// carriers.personal.name = "Telecom Personal"

		Path path = Paths.get(SAMPLE_PROPERTIES_3);
		when(fileContentProvider.readContents(path)).thenReturn(getFileContents(SAMPLE_PROPERTIES_3));

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

	private List<String> getFileContents(String path) throws IOException {
		File file = FileUtils.toFile(this.getClass().getClassLoader().getResource(path));
		List<String> lines = new LinkedList<>();
		try (LineIterator iterator = FileUtils.lineIterator(file)) {
			iterator.forEachRemaining(l -> lines.add(l));
		}
		return lines;
	}
}