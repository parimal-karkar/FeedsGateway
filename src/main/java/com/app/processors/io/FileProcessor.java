package com.app.processors.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface FileProcessor {
    void processFile() throws IOException;

    default void validateFilePath(Path filePath) {
        File file = filePath.toFile();
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            String errorMessage = "Invalid file path provided or user don't have necessary permissions";
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
