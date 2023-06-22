package com.dlim2012.clients.exception;

public class NotAuthorizedException extends RuntimeException{

    public NotAuthorizedException(String message) {
        super(message);
    }
}
