package com.fitness.app.exceptions;

public class InvalidUsernameException extends IllegalArgumentException {

    public InvalidUsernameException(String message) {
        super(message);
    }
}
