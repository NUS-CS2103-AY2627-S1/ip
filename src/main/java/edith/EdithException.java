package edith;

/**
 * Represents an error caused by an invalid Edith command.
 */
public class EdithException extends Exception {
    /**
     * Creates an exception with a message suitable for display to the user.
     *
     * @param message the explanation of the invalid command
     */
    public EdithException(String message) {
        super(message);
    }
}
