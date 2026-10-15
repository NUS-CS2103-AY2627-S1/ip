package kdb;

/** Represents an expected error caused by invalid chatbot input. */
public class KdbException extends Exception {
    /**
     * Creates an exception with a message suitable for displaying to the user.
     *
     * @param message explanation of the invalid command or input
     */
    public KdbException(String message) {
        super(message);
    }

    /** Creates an exception with a message and the underlying cause. */
    public KdbException(String message, Throwable cause) {
        super(message, cause);
    }
}
