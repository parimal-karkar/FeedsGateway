package com.app.dao;

import com.app.config.Constants;
import com.app.entity.EventEntity;
import com.app.exceptions.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class EventDao extends BaseDao {
    private static final Logger LOG = LoggerFactory.getLogger(EventDao.class);

    public EventDao() {
        super();
        try (Connection conn = getConnection()) {
            LOG.info("Created table {}", conn.createStatement().executeUpdate(Constants.CREATE_TABLE));
        } catch (SQLException ex) {
            LOG.error("Couldn't create table", ex);
            throw new DataAccessException("Error occurred while creating event table", ex);
        }
    }

    public void save(EventEntity eventEntity, boolean update) {
        try (Connection conn = getConnection()) {
            if (!update) {
                try(PreparedStatement statement = conn.prepareStatement(Constants.SQL_INSERT_INTO_EVENT)){
                    statement.setString(1, eventEntity.getEventId());
                    statement.setTimestamp(2, eventEntity.getStartTime() != null ? new Timestamp(eventEntity.getStartTime().toEpochMilli()) : null);
                    statement.setTimestamp(3, eventEntity.getEndTime() != null ? new Timestamp(eventEntity.getEndTime().toEpochMilli()) : null);
                    statement.setString(4, eventEntity.getHost());
                    statement.setString(5, eventEntity.getType());
                    statement.setBoolean(6, eventEntity.getAlert());
                    statement.executeUpdate();
                    LOG.debug("Saved entity: {} ", eventEntity);
                }
            } else {
                try(PreparedStatement statement = conn.prepareStatement(Constants.SQL_UPDATE_EVENT)) {
                    statement.setString(6, eventEntity.getEventId());
                    statement.setTimestamp(1, eventEntity.getStartTime() != null ? new Timestamp(eventEntity.getStartTime().toEpochMilli()) : null);
                    statement.setTimestamp(2, eventEntity.getEndTime() != null ? new Timestamp(eventEntity.getEndTime().toEpochMilli()) : null);
                    statement.setString(3, eventEntity.getHost());
                    statement.setString(4, eventEntity.getType());
                    statement.setBoolean(5, eventEntity.getAlert());
                    statement.executeUpdate();
                    LOG.debug("updated entity: {}", eventEntity);
                }
            }
        } catch (SQLException ex) {
            LOG.error("Error occurred while saving event entity", ex);
            throw new DataAccessException("Error occurred while getting event entity", ex);
        }
    }

    public EventEntity get(String id) {
        EventEntity eventEntity = null;
        try (Connection conn = getConnection()) {
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            try (PreparedStatement statement = conn.prepareStatement(Constants.SQL_SELECT_EVENT)) {
                statement.setString(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        eventEntity = mapEventEntity(resultSet);
                    }
                }
            }
            return eventEntity;
        } catch (SQLException ex) {
            LOG.error("Error occurred while saving event entity", ex);
            throw new DataAccessException("Error occurred while getting event entity", ex);
        }
    }

    private EventEntity mapEventEntity(ResultSet resultSet) throws SQLException {
        EventEntity entity = new EventEntity();
        entity.setEventId(resultSet.getString("id"));
        Timestamp startTime = resultSet.getTimestamp("start_time");
        if (startTime != null) {
            entity.setStartTime(startTime.toInstant());
        }
        Timestamp endTime = resultSet.getTimestamp("end_time");
        if (endTime != null) {
            entity.setEndTime(endTime.toInstant());
        }
        entity.setHost(resultSet.getString("host"));
        entity.setType(resultSet.getString("type"));
        entity.setAlert(resultSet.getBoolean("is_alert"));
        LOG.debug("Fetched entity {}", entity);
        return entity;
    }
}
