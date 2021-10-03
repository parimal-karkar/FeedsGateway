package com.app.config;

public class Constants {
    public static final String JDBC_HSQLDB_URL = "jdbc:hsqldb:file:eventdb;";
    public static final String DB_USER = "SA";
    public static final String DB_PASSWORD = "";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS event (\n" +
            "   id VARCHAR(250) NOT NULL,\n" +
            "   start_time TIMESTAMP,\n" +
            "   end_time TIMESTAMP,\n" +
            "   type varchar(100),\n" +
            "   host varchar(100) ,\n" +
            "   is_alert boolean,\n" +
            "   PRIMARY KEY (id) \n" +
            ");";
    public static final String SQL_INSERT_INTO_EVENT = "insert into event values(?,?,?,?,?,?)";
    public static final String SQL_UPDATE_EVENT = "update event set start_time=?,end_time=?,host=?,type=?,is_alert=? where id=?";
    public static final String SQL_SELECT_EVENT = "select * from event where id=?";
    public static final int DURATION_THRESHOLD_MILLI_SEC = 4;
}
