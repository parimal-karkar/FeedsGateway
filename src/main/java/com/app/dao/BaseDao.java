package com.app.dao;

import com.app.config.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * BaseDao class to facilitate common functionality
 */
public abstract class BaseDao {
    private final String db;
    private final String user;
    private final String password;

    public BaseDao() {
        this.db = Constants.JDBC_HSQLDB_URL;
        this.user = Constants.DB_USER;
        this.password = Constants.DB_PASSWORD;
    }

    protected Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(db, user, password);
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        return connection;
    }
}
