package com.app.exceptions;

public class EventProcessingException extends RuntimeException{
    public EventProcessingException(){
        super();
    }
    public EventProcessingException(String errorMessage){
        super(errorMessage);
    }
}
