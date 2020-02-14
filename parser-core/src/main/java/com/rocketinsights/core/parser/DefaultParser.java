package com.rocketinsights.core.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rocketinsights.core.event.Event;
import com.rocketinsights.core.event.EventLog;
import com.rocketinsights.core.event.EventType;
import com.rocketinsights.core.event.SimpleEventLog;
import com.rocketinsights.core.event.handler.DocumentEventHandler;
import com.rocketinsights.core.event.handler.DocumentEventHandlerFactory;
import com.rocketinsights.core.event.handler.EventHandler;
import com.rocketinsights.core.exception.ProcessingException;
import com.rocketinsights.core.reader.Reader;
import com.rocketinsights.core.reader.ReaderFactory;

public class DefaultParser implements Parser, EventHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultParser.class);

	private Writer writer;

	@Override
	public void parse(String inputFile, String outputFile, SupportedFormat format)
			throws ProcessingException, IOException {
		boolean failed = false;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Paths.get(outputFile).toFile()),
					StandardCharsets.UTF_8));

			DocumentEventHandler documentHandler = new DocumentEventHandlerFactory().createEventHandler(format, writer);
			LOG.info("Document handler [{}] found for format [{}]", documentHandler.getClass().getSimpleName(), format);
			EventLog eventLog = new SimpleEventLog();
			eventLog.addHandler(documentHandler);
			eventLog.addHandler(this);

			// read the file & start writing events in the log
			Reader reader = new ReaderFactory().createReader(inputFile);
			LOG.info("Document reader [{}] found for file [{}]", reader.getClass().getSimpleName(), inputFile);
			reader.read(Paths.get(inputFile), eventLog);
		} catch (ProcessingException | IOException e) {
			failed = true;
			throw e;
		} finally {
			writer.close();
			File file = Paths.get(outputFile).toFile();
			if ((file.exists() && file.length() == 0L) || failed) {
				LOG.debug("Will try to remove the output file [{}] as the process failed at some point", outputFile);
				Files.delete(file.toPath());
			}
		}

	}

	@Override
	public void handle(Event event) throws IOException {
		if (EventType.DOC_ENDED.equals(event.getEventType())) {
			writer.flush();
		}
	}

}
