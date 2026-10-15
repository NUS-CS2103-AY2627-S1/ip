package exception;

/**
 * Signals that an operation was attempted on an empty task list.
 */
public class ListEmptyException extends RuntimeException {

    /**
     * Creates a new exception with the provided message.
     *
     * @param message the error details
     */
    public ListEmptyException(String message) {
        super(message);
    }
}
