package com.app.processors.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorProcessor {
    static final Logger DLQ = LoggerFactory.getLogger(ErrorProcessor.class);

    public void processErrorRecord(String filePath, String errorRecord){
        DLQ.error("Encountered bad record for file: {}, record: {}",filePath, errorRecord);
    }
}
