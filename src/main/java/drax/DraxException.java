package drax;

/**
 * Signals an invalid task-manager operation that can be reported to the user.
 */
public class DraxException extends Exception {
    public DraxException() {
        super();
    }

    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message explanation of the invalid operation.
     */
    public DraxException(String message) {
        super(message);
    }

}
