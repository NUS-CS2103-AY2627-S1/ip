package doge.exception;

/** Represents an expected error that can be shown to the Doge user. */
public class DogeException extends Exception {
    /** Creates an exception with a user-facing error message. */
    public DogeException(String message) {
        super(message);
    }
}
