package nudge.exception;

/**
 * Represents an input error that Nudge can explain to the user.
 */
public class NudgeException extends Exception {

    /**
     * Creates an exception with the specified user-facing explanation.
     *
     * @param message explanation of the input error.
     */
    public NudgeException(String message) {
        super(message);
    }
}
