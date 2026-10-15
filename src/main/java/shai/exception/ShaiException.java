package shai.exception;

/** Represents an expected application error that can be shown to the user. */
public class ShaiException extends Exception {
    /** Creates an application exception with the specified message. */
    public ShaiException(String message) {
        super(message);
    }
}
