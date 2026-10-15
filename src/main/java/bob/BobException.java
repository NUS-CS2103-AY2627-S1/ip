package bob;

/**
 * Indicates that a user command is invalid and contains a user-facing explanation.
 */
public class BobException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception for an invalid command.
     *
     * @param message explanation to show the user
     */
    public BobException(String message) {
        super(message);
    }
}
