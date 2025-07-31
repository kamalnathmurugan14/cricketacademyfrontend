package com.cricketacademy.api.exception;

/**
 * Exception thrown when a user is already logged in
 */
public class UserAlreadyLoggedInException extends RuntimeException {

    public UserAlreadyLoggedInException(String message) {
        super(message);
    }

    public UserAlreadyLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }
}