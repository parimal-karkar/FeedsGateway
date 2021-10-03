package com.app.processors.io;

import com.app.exceptions.EventProcessingException;
import com.app.processors.error.ErrorProcessor;
import com.app.processors.event.EventProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JsonFileProcessorTest {

    @Mock
    EventProcessor eventProcessor;

    @Mock
    Path filePath;

    @Mock
    ErrorProcessor errorProcessor;

    @InjectMocks
    JsonFileProcessor processor;

    @Test
    void givenValidFile_whenProcessFile_thenSucceed() {
        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.isFile()).thenReturn(true);
        when(mockFile.canRead()).thenReturn(true);
        when(filePath.toFile()).thenReturn(mockFile);
        doNothing().when(eventProcessor).processEvent(anyString());

        String eventString = "{\"id\":\"scsmbstgrc\",\"state\":\"STARTED\",\"timestamp\":1491377495210}";
        Stream<String> eventStream = Stream.of(eventString);

        try (MockedStatic<Files> files = mockStatic(Files.class)) {
            files.when(() -> Files.lines(any())).thenReturn(eventStream);
            assertDoesNotThrow(() -> processor.processFile());
            verify(eventProcessor,times(1)).processEvent(anyString());
        }
    }

    @Test
    void givenFileWithoutReadPermission_whenProcessFile_thenShouldThrowIllegalArgumentException() {
        File mockFile = mock(File.class);

        when(mockFile.exists()).thenReturn(true);
        when(mockFile.isFile()).thenReturn(true);
        when(mockFile.canRead()).thenReturn(false);

        when(filePath.toFile()).thenReturn(mockFile);

        assertThrows(IllegalArgumentException.class,() -> processor.processFile());
    }

    @Test
    void givenValidEvent_whenProcessEvent_thenSucceed() {
        String eventString = "{\"id\":\"scsmbstgrc\",\"state\":\"STARTED\",\"timestamp\":1491377495210}";
        doNothing().when(eventProcessor).processEvent(anyString());

        processor.processEvent(eventString);

        verify(eventProcessor, times(1)).processEvent(anyString());
        verify(errorProcessor, times(0)).processErrorRecord(anyString(), anyString());
    }


    @Test
    void givenInValidEvent_whenProcessEvent_thenRouteToDLQ() {
        String eventString = "{idscsmbstgrc\"\"timestamp\":1491377495210}";

        doThrow(EventProcessingException.class).when(eventProcessor).processEvent(anyString());
        doNothing().when(errorProcessor).processErrorRecord(anyString(), anyString());


        processor.processEvent(eventString);

        verify(eventProcessor, times(1)).processEvent(anyString());
        verify(errorProcessor, times(1)).processErrorRecord(anyString(), anyString());
    }
}