package com.app.dao;

import com.app.config.Constants;
import com.app.entity.EventEntity;
import com.app.exceptions.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class EventDao {
    private static final Logger LOG = LoggerFactory.getLogger(EventDao.class);

    private final String db;
    private final String user;
    private final String password;

    public EventDao() {
        this.db = Constants.JDBC_HSQLDB_URL;
        this.user = Constants.DB_USER;
        this.password = Constants.DB_PASSWORD;
        try (Connection conn = DriverManager.getConnection(db, user, password)) {
            LOG.debug("Created table {}", conn.createStatement().executeUpdate(Constants.CREATE_TABLE));
        } catch (SQLException ex) {
            LOG.error("Couldn't create table", ex);
            throw new DataAccessException("Error occurred while creating event table");
        }
    }

    public void save(EventEntity eventEntity, boolean update) {
        try (Connection conn = DriverManager.getConnection(db, user, password)) {

            if (!update) {
                PreparedStatement statement = conn.prepareStatement(Constants.SQL_INSERT_INTO_EVENT);
                statement.setString(1, eventEntity.getEventId());
                statement.setTimestamp(2, eventEntity.getStartTime() != null ? new Timestamp(eventEntity.getStartTime().toEpochMilli()) : null);
                statement.setTimestamp(3, eventEntity.getEndTime() != null ? new Timestamp(eventEntity.getEndTime().toEpochMilli()) : null);
                statement.setString(4, eventEntity.getHost());
                statement.setString(5, eventEntity.getType());
                statement.setBoolean(6, eventEntity.getAlert());
                statement.executeUpdate();
                LOG.debug("Saved entity: {} ", eventEntity);
            } else {
                PreparedStatement statement = conn.prepareStatement(Constants.SQL_UPDATE_EVENT);
                statement.setString(6, eventEntity.getEventId());
                statement.setTimestamp(1, eventEntity.getStartTime() != null ? new Timestamp(eventEntity.getStartTime().toEpochMilli()) : null);
                statement.setTimestamp(2, eventEntity.getEndTime() != null ? new Timestamp(eventEntity.getEndTime().toEpochMilli()) : null);
                statement.setString(3, eventEntity.getHost());
                statement.setString(4, eventEntity.getType());
                statement.setBoolean(5, eventEntity.getAlert());
                statement.executeUpdate();
                LOG.debug("updated entity: {}", eventEntity);
            }
        } catch (SQLException ex) {
            LOG.error("Error occurred while saving event entity", ex);
            throw new DataAccessException("Error occurred while getting event entity");
        }
    }

    public EventEntity get(String id) {
        EventEntity eventEntity = null;
        try (Connection conn = DriverManager.getConnection(db, user, password)) {
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
            throw new DataAccessException("Error occurred while getting event entity");
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
