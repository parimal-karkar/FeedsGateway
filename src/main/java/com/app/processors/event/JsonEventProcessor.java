package com.app.processors.event;

import com.app.config.Constants;
import com.app.dao.EventDao;
import com.app.dtos.Event;
import com.app.dtos.State;
import com.app.entity.EventEntity;
import com.app.exceptions.EventProcessingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

final public class JsonEventProcessor implements EventProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(JsonEventProcessor.class);
    private final ObjectMapper mapper;
    private final EventDao dao;

    public JsonEventProcessor(EventDao dao) {
        this.mapper = new ObjectMapper();
        this.dao = dao;
    }

    @Override
    public void processEvent(String eventString) {
        try {
            Event event = mapper.readValue(eventString, Event.class);
            LOG.trace("Parsed event {}",event);
            EventEntity entity = dao.get(event.getId());

            boolean update = entity!=null;
             entity = getMappedEntity(event,entity);
             dao.save(entity, update);

        } catch (JsonProcessingException e) {
            LOG.error("Error occurred while parsing event ",e);
            throw new EventProcessingException(e.getMessage());
        }
    }

    private EventEntity getMappedEntity(Event event, EventEntity eventEntity) {
        EventEntity entity = eventEntity!=null?eventEntity:new EventEntity();
        entity.setEventId(event.getId());
        if(State.STARTED.equals(event.getState())){
            entity.setStartTime(event.getTimestamp().toInstant());
        }
        else{
            entity.setEndTime(event.getTimestamp().toInstant());
        }
        entity.setType(event.getType());
        entity.setHost(event.getHost());
        if(entity.getStartTime()!=null && entity.getEndTime()!=null){
            long duration = Duration.between(entity.getStartTime(), entity.getEndTime()).toMillis();
            entity.setAlert(duration> Constants.DURATION_THRESHOLD_MILLI_SEC);
        }
        return entity;
    }
}
