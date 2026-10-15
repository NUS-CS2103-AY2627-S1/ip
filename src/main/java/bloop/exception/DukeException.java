package bloop.exception;

/**
 * Represents an error caused by invalid input to the Duke chatbot.
 */
public class DukeException extends Exception {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message explanation of the invalid input
     */
    public DukeException(String message) {
        super(message);
    }
}
