package com.rocketinsights;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.rocketinsights.core.exception.ProcessingException;
import com.rocketinsights.core.parser.Parser;
import com.rocketinsights.core.parser.DefaultParser;
import com.rocketinsights.core.parser.SupportedFormat;

@SpringBootApplication
public class App implements CommandLineRunner {

	private static final String UTILITY_NAME = "Rocket Insights - Properties parser";

	private static final Logger LOG = LoggerFactory.getLogger(App.class);

	private static final String INPUT_SHORT = "i";
	private static final String INPUT_LONG = "input";
	private static final String OUTPUT_SHORT = "o";
	private static final String OUTPUT_LONG = "output";
	private static final String FORMAT_SHORT = "f";
	private static final String FORMAT_LONG = "format";

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			parse(args);
		} catch (Exception e) {
			LOG.error("An error occurred while executing the process. Check the messages above to get more information");
			System.exit(1);
		}
	}

	private static void parse(String[] args) throws ProcessingException, IOException, ParseException {

		CommandLine commandLine = readAndValidateCmd(args);

		String inputFile = commandLine.getOptionValue(INPUT_LONG);
		String outputFile = commandLine.getOptionValue(OUTPUT_LONG);

		Parser parser = new DefaultParser();
		SupportedFormat format = SupportedFormat.valueOf(commandLine.getOptionValue(FORMAT_LONG));
		LOG.info("Starting parsing process for input file [{}], output file [{}] and format [{}]", inputFile,
				outputFile, format);
		parser.parse(inputFile, outputFile, format);

		LOG.info("Parsing process ended SUCCESSFULLY. File [{}] was created.", outputFile);
	}

	private static CommandLine readAndValidateCmd(String[] args) throws ParseException {
		Options options = new Options();

		Option input = new Option(INPUT_SHORT, INPUT_LONG, true, "input file path");
		input.setRequired(true);
		options.addOption(input);

		Option output = new Option(OUTPUT_SHORT, OUTPUT_LONG, true, "output file");
		output.setRequired(true);
		options.addOption(output);

		Option format = new Option(FORMAT_SHORT, FORMAT_LONG, true, "format - one of XML, JSON or PROPERTY.");
		format.setRequired(true);
		options.addOption(format);

		try {
			CommandLineParser commandLineParser = new org.apache.commons.cli.DefaultParser();
			CommandLine commandLine = commandLineParser.parse(options, args);

			String f = commandLine.getOptionValue(FORMAT_LONG);

			for (SupportedFormat supportedFormat : SupportedFormat.values()) {
				if (supportedFormat.name().equals(f)) {
					return commandLine;
				}
			}

			throw new ParseException("Invalid value for arg f.");
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(UTILITY_NAME, options);
			throw e;
		}
	}
}
