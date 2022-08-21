package com.app.exceptions;

public class DataAccessException extends RuntimeException{
    public DataAccessException() {
        super();
    }

    public DataAccessException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
