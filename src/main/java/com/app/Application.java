package com.app;

import com.app.dao.EventDao;
import com.app.processors.error.ErrorProcessor;
import com.app.processors.event.EventProcessor;
import com.app.processors.event.JsonEventProcessor;
import com.app.processors.io.FileProcessor;
import com.app.processors.io.JsonFileProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Application main entry point
 * accepts file path as program argument or as command line argument
 */
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            processFile(args[0]);
            return;
        }
        System.out.println("Provide absolute file path to process or type 'exit' to stop program ");
        try (Scanner sc = new Scanner(System.in)) {
            while (sc.hasNext()) {
                String input = sc.next();
                if ("exit".equalsIgnoreCase(input)) {
                    break;
                } else {
                    processFile(input);
                }
            }
            System.out.println("Stopping program ...");
        } catch (Exception e) {
            LOG.error("Error occurred while processing file.", e);
        }
    }

    private static void processFile(String input) throws IOException {
        EventDao dao = new EventDao();
        Path filePath = Path.of(input);
        ErrorProcessor errorProcessor = new ErrorProcessor();
        EventProcessor eventProcessor = new JsonEventProcessor(dao);
        FileProcessor fileProcessor = new JsonFileProcessor(eventProcessor, filePath, errorProcessor);
        fileProcessor.processFile();
    }
}
