package com.rocketinsights.core.exception;

public class InvalidFormatException extends RuntimeException {

	private static final long serialVersionUID = -4060976564673492220L;

	public InvalidFormatException(String message) {
		super(message);
	}

	public InvalidFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
