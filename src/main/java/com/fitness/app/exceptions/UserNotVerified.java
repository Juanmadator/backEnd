package com.fitness.app.exceptions;

public class UserNotVerified  extends IllegalArgumentException{

    public UserNotVerified(String message) {
        super(message);
    }
}
