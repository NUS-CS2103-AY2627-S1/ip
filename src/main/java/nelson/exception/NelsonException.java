package nelson.exception;

/**
 * Represents an invalid command or command argument entered into Nelson.
 */
public class NelsonException extends Exception {
    /**
     * Creates an exception with a message Nelson can show to the user.
     *
     * @param message the explanation of the invalid command
     */
    public NelsonException(String message) {
        super(message);
    }
}
