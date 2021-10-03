package com.app.processors.event;

import com.app.dao.EventDao;
import com.app.entity.EventEntity;
import com.app.exceptions.EventProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JsonEventProcessorTest {

    @Mock
    EventDao dao;

    @InjectMocks
    JsonEventProcessor eventProcessor;

    @Captor
    ArgumentCaptor<EventEntity> entityArgCaptor;

    @Test
    void givenValidEventString_whenProcessEvent_thenSucceed() {
        String eventString = "{\"id\":\"scsmbstgrc\",\"state\":\"STARTED\",\"timestamp\":1491377495210}";
        doNothing().when(dao).save(any(EventEntity.class), any(Boolean.class));
        when(dao.get(anyString())).thenReturn(nullable(EventEntity.class));

        eventProcessor.processEvent(eventString);

        verify(dao, times(1)).save(any(EventEntity.class), any(Boolean.class));
    }


    @Test
    void givenInValidEventString_whenProcessEvent_thenShouldThrowEventProcessingException() {
        String eventString = "{\"idscsmbstgrc\",\"state\":\"STARTED\",\"timestamp\":1491377495210}";

        assertThrows(EventProcessingException.class, () -> eventProcessor.processEvent(eventString));

        verify(dao, times(0)).save(any(EventEntity.class), any(Boolean.class));
    }

    @Test
    void givenValidEventStringWithDurationMoreThanThreshold_whenProcessEvent_thenSucceed() {
        String eventString = "{\"id\":\"scsmbstgrc\",\"state\":\"STARTED\",\"timestamp\":1491377495210}";
        doNothing().when(dao).save(any(EventEntity.class), any(Boolean.class));
        EventEntity entity = new EventEntity();
        entity.setEventId("scsmbstgrc");
        entity.setEndTime(Instant.ofEpochMilli(1491377495218L));
        when(dao.get(anyString())).thenReturn(entity);

        eventProcessor.processEvent(eventString);

        verify(dao, times(1)).save(entityArgCaptor.capture(), any(Boolean.class));
        assertTrue(entityArgCaptor.getValue().getAlert());
    }


    @Test
    void givenValidEventStringWithDurationLessThanThreshold_whenProcessEvent_thenSucceed() {
        String eventString = "{\"id\":\"scsmbstgrc\",\"state\":\"FINISHED\",\"timestamp\":1491377495216}";
        doNothing().when(dao).save(any(EventEntity.class), any(Boolean.class));
        EventEntity entity = new EventEntity();
        entity.setEventId("scsmbstgrc");
        entity.setStartTime(Instant.ofEpochMilli(1491377495213L));
        when(dao.get(anyString())).thenReturn(entity);

        eventProcessor.processEvent(eventString);

        verify(dao, times(1)).save(entityArgCaptor.capture(), any(Boolean.class));
        assertFalse(entityArgCaptor.getValue().getAlert());
    }
}
