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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.rocketinsights.core.event.handler.PropertyEventHandler;
import com.rocketinsights.core.exception.ProcessingException;

public class PropertyEventHandlerTest {

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	private PropertyEventHandler underTest;

	@Test
	public void testRead_ioError() throws IOException, ProcessingException {
		exceptionRule.expect(ProcessingException.class);
		exceptionRule.expectCause(isA(IOException.class));

		ProcessingException ex = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (Writer writer = new OutputStreamWriter(out, "UTF-8")) {
			underTest = new PropertyEventHandler(writer);

			writer.close();

			underTest.handle(new Event(EventType.DOC_STARTED));
			underTest.handle(new Event(EventType.NODE_OPENED, "carriers", null));

		} catch (ProcessingException e) {
			ex = e;
		}
		throw ex;
	}

	@Test
	public void test_properties1() throws UnsupportedEncodingException, IOException, ProcessingException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (Writer writer = new OutputStreamWriter(out, "UTF-8")) {
			underTest = new PropertyEventHandler(writer);

			// carriers.personal.name = "Telecom Personal"
			// carriers.personal.country = "Argentina"
			// carriers.personal.address = "Av. Corrientes 566"
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
			underTest.handle(new Event(EventType.NODE_CLOSED, "carriers", null));
			underTest.handle(new Event(EventType.DOC_ENDED));

			StringBuilder expected = new StringBuilder();
			expected.append("carriers.personal.name = \"Telecom Personal\"").append("\n");
			expected.append("carriers.personal.country = \"Argentina\"").append("\n");
			expected.append("carriers.personal.address = \"Av. Corrientes 566\"");

			writer.flush();
			assertThat(new String(out.toByteArray()), is(equalTo(expected.toString())));
		}
	}

	@Test
	public void test_properties2() throws UnsupportedEncodingException, IOException, ProcessingException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (Writer writer = new OutputStreamWriter(out, "UTF-8")) {
			underTest = new PropertyEventHandler(writer);

			// restaurants.brigada.name = "La Brigada"
			// restaurants.brigada.location.country = "Argentina"
			// restaurants.brigada.location.neighborhood = "San Telmo"
			// restaurants.brigada.location.address = "Estados Unidos 465"
			// restaurants.viejoalmacen.name = "El Viejo Almacen"
			// restaurants.viejoalmacen.phone = "011 4307-7388"
			// restaurants.viejoalmacen.location.country = "Argentina"
			// restaurants.viejoalmacen.location.neighborhood = "San Telmo"
			// restaurants.viejoalmacen.location.address = "Av. Independencia 299"
			underTest.handle(new Event(EventType.DOC_STARTED));
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
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "Estados Unidos 465"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "address", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "location", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "brigada", null));

			underTest.handle(new Event(EventType.NODE_OPENED, "viejoalmacen", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "name", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "El Viejo Almacen"));
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
			StringBuilder expected = new StringBuilder();
			expected.append("restaurants.brigada.name = \"La Brigada\"").append("\n");
			expected.append("restaurants.brigada.location.country = \"Argentina\"").append("\n");
			expected.append("restaurants.brigada.location.neighborhood = \"San Telmo\"").append("\n");
			expected.append("restaurants.brigada.location.address = \"Estados Unidos 465\"").append("\n");
			expected.append("restaurants.viejoalmacen.name = \"El Viejo Almacen\"").append("\n");
			expected.append("restaurants.viejoalmacen.phone = \"011 4307-7388\"").append("\n");
			expected.append("restaurants.viejoalmacen.location.country = \"Argentina\"").append("\n");
			expected.append("restaurants.viejoalmacen.location.neighborhood = \"San Telmo\"").append("\n");
			expected.append("restaurants.viejoalmacen.location.address = \"Av. Independencia 299\"");

			writer.flush();
			assertThat(new String(out.toByteArray()), is(equalTo(expected.toString())));
		}
	}

	@Test
	public void test_properties3() throws UnsupportedEncodingException, IOException, ProcessingException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (Writer writer = new OutputStreamWriter(out, "UTF-8")) {
			underTest = new PropertyEventHandler(writer);

			// restaurants.brigada.location.address = "Estados Unidos 465"
			// carriers.personal.name = "Telecom Personal"
			underTest.handle(new Event(EventType.DOC_STARTED));
			underTest.handle(new Event(EventType.NODE_OPENED, "restaurants", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "brigada", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "location", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "address", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "Estados Unidos 465"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "address", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "location", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "brigada", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "restaurants", null));

			underTest.handle(new Event(EventType.NODE_OPENED, "carriers", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "personal", null));
			underTest.handle(new Event(EventType.NODE_OPENED, "name", null));
			underTest.handle(new Event(EventType.VALUE_ADDED, null, "Telecom Personal"));
			underTest.handle(new Event(EventType.NODE_CLOSED, "name", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "personal", null));
			underTest.handle(new Event(EventType.NODE_CLOSED, "carriers", null));

			underTest.handle(new Event(EventType.DOC_ENDED));
			StringBuilder expected = new StringBuilder();
			expected.append("restaurants.brigada.location.address = \"Estados Unidos 465\"").append("\n");
			expected.append("carriers.personal.name = \"Telecom Personal\"");

			writer.flush();
			assertThat(new String(out.toByteArray()), is(equalTo(expected.toString())));
		}
	}
}
