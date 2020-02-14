package com.rocketinsights.core.event.handler;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.Writer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.rocketinsights.core.parser.SupportedFormat;

@RunWith(MockitoJUnitRunner.class)
public class DocumentEventHandlerFactoryTest {

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	private DocumentEventHandlerFactory underTest;

	@Mock
	Writer writer;

	@Before
	public void setUp() {
		underTest = new DocumentEventHandlerFactory();
	}

	@Test
	public void test_createNullWriter() throws IOException {
		exceptionRule.expect(IllegalArgumentException.class);

		underTest.createEventHandler(SupportedFormat.JSON, null);
	}

	@Test
	public void test_createUnsupportedType() throws IOException {
		exceptionRule.expect(IllegalArgumentException.class);

		underTest.createEventHandler(null, writer);
	}

	@Test
	public void test_createXmlFactory() throws IOException {
		DocumentEventHandler handler = underTest.createEventHandler(SupportedFormat.XML, writer);
		assertThat(handler, is(instanceOf(XmlEventHandler.class)));
	}

	@Test
	public void test_createJsonFactory() throws IOException {
		DocumentEventHandler handler = underTest.createEventHandler(SupportedFormat.JSON, writer);
		assertThat(handler, is(instanceOf(JsonEventHandler.class)));
	}

	@Test
	public void test_createPropertyFactory() throws IOException {
		DocumentEventHandler handler = underTest.createEventHandler(SupportedFormat.PROPERTY, writer);
		assertThat(handler, is(instanceOf(PropertyEventHandler.class)));
	}

}
