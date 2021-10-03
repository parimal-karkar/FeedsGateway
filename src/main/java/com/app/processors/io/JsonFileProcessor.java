package com.app.processors.io;

import com.app.exceptions.EventProcessingException;
import com.app.processors.error.ErrorProcessor;
import com.app.processors.event.EventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class JsonFileProcessor implements FileProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(JsonFileProcessor.class);

    private final EventProcessor eventProcessor;

    private final ErrorProcessor errorProcessor;

    private final Path filePath;

    public JsonFileProcessor(EventProcessor eventProcessor, Path filePath, ErrorProcessor errorProcessor) {
        this.eventProcessor = eventProcessor;
        this.filePath = filePath;
        this.errorProcessor = errorProcessor;
    }

    @Override
    public void processFile() throws IOException {
        LOG.info("Validating file path: {}", filePath);
        validateFilePath(filePath);
        LOG.info("Starting record processing for file: {}", filePath);
        try (Stream<String> eventStream = Files.lines(filePath)) {
            eventStream.forEach(this::processEvent);
        }
        LOG.info("Finished record processing for file: {}", filePath);
    }

    void processEvent(String eventString) {
        try {
            eventProcessor.processEvent(eventString);
        } catch (EventProcessingException ex) {
            errorProcessor.processErrorRecord(filePath.toString(), eventString);
        }
    }
}
