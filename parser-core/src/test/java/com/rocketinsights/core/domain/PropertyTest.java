package com.rocketinsights.core.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.rocketinsights.core.exception.InvalidFormatException;

public class PropertyTest {

	@Test
	public void testOf_validProperty() {
		List<String> path = new LinkedList<>();
		path.add("carriers");
		path.add("personal");
		path.add("name");
		String value = "Telecom Personal";

		Property property = Property.of("carriers.personal.name = \"Telecom Personal\"");

		assertThat(property.getPath(), equalTo(path));
		assertThat(property.getValue(), equalTo(value));

	}

	@Test
	public void testOf_validPropert2y() {
		List<String> path = new LinkedList<>();
		path.add("carriers");
		String value = "Telecom Personal";

		Property property = Property.of("carriers = \"Telecom Personal\"");

		assertThat(property.getPath(), equalTo(path));
		assertThat(property.getValue(), equalTo(value));

	}

	@Test(expected = InvalidFormatException.class)
	public void testOf_invalidProperty() {

		Property.of("carriers.personal.= \"Telecom Personal\"");
	}

	@Test(expected = InvalidFormatException.class)
	public void testOf_invalidProperty2() {
		Property.of("carriers..personal= \"Telecom Personal\"");
	}

	@Test(expected = InvalidFormatException.class)
	public void testOf_invalidProperty3() {
		Property.of("carriers...personal. = \"Telecom Personal\"");
	}

	@Test(expected = InvalidFormatException.class)
	public void testOf_invalidProperty4() {
		Property.of("carriers.personal = Telecom Personal");
	}

	@Test(expected = InvalidFormatException.class)
	public void testOf_invalidProperty5() {
		Property.of("carriers. = \"Telecom Personal\"");
	}

}
