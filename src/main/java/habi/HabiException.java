package habi;

/**
 * Represents a recoverable error in a command entered for HABI.
 */
public class HabiException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message Explanation to display to the user.
     */
    public HabiException(String message) {
        super(message);
    }
}
