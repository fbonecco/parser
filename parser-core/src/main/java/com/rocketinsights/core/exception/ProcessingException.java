package com.rocketinsights.core.exception;

public class ProcessingException extends Exception {

	private static final long serialVersionUID = 9206560385572341436L;

	public ProcessingException(String message) {
		super(message);
	}

	public ProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

}
