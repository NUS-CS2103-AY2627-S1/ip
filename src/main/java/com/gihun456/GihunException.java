package com.gihun456;

/**
 * Custom exception for the Gihun456 application.
 */
public class GihunException extends Exception {
    public GihunException(String message) {
        super(message);
    }

    public GihunException(String message, Throwable cause) {
        super(message, cause);
    }
}
