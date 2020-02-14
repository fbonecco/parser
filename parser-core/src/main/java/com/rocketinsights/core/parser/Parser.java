package com.rocketinsights.core.parser;

import java.io.IOException;

import com.rocketinsights.core.exception.ProcessingException;

/**
 * A {@link Parser} is in charge of orchestrating a parsing/conversion process.
 * 
 * @author fbonecco
 *
 */
public interface Parser {

	/**
	 * Run the parsing process. If an IO error occurs an {@link IOException} is
	 * thrown. Also, if the format of the input file is incorrect, a
	 * {@link ProcessingException} can be thrown.
	 * 
	 * @param inputFile  the source file
	 * @param outputFile the destination file
	 * @param format     type of the conversion. this will be used while creating
	 *                   the output file.
	 * @throws ProcessingException
	 * @throws IOException
	 */
	public void parse(String inputFile, String outputFile, SupportedFormat format)
			throws ProcessingException, IOException;
}
