package lebron.exception;

/**
 * Signals an expected, user-facing problem (e.g. a task number that is out
 * of range). The message is written so it can be shown to the user as-is.
 */
public class LebronException extends Exception {
    /**
     * Creates an exception whose message can be shown to the user as-is.
     *
     * @param message the user-facing description of the problem
     */
    public LebronException(String message) {
        super(message);
    }
}
