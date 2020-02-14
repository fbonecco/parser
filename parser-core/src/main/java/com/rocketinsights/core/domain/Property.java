package com.rocketinsights.core.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rocketinsights.core.exception.InvalidFormatException;

public class Property {

	private static final String VALIDATION_REGEX = "^(?!.*?\\.\\.)([\\w\\.]+?)(?<!\\.)(?<!\\.{2,})\\s*=\\s*\\\"([^\\\"]+?)\\\"\\s*$";

	private static final Pattern PATTERN = Pattern.compile(VALIDATION_REGEX);

	private static final String POINT_REGEX = "\\.";

	private List<String> path;

	private String value;

	private Property() {
	}

	public static Property of(String value) {
		Matcher matcher = PATTERN.matcher(value);
		Property property = new Property();
		if (matcher.matches()) {
			List<String> split = Collections.unmodifiableList(Arrays.asList(matcher.group(1).split(POINT_REGEX)));
			property.path = split;
			property.value = matcher.group(2);
			return property;
		}

		throw new InvalidFormatException(
				String.format("The format of property %s looks invalid. Please check.", value));
	}

	public List<String> getPath() {
		return path;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {

		if (o == null)
			return false;
		if (this == o)
			return true;
		if (!o.getClass().equals(getClass()))
			return false;
		Property obj = (Property) o;
		return Objects.equals(path, obj.path) && Objects.equals(value, obj.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(path, value);
	}

	@Override
	public String toString() {
		return "Property [path=" + path + ", value=" + value + "]";
	}
}
