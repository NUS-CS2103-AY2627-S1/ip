package panda.exception;

/**
 * Base type for user-facing command errors in Panda.
 *
 * Gives expected input errors a shared checked-exception type
 * so the UI can report them without hiding unrelated programming errors.
 */
public abstract class PandaException extends Exception {
    /**
     * Creates an error with the message that should be shown to the user.
     *
     * @param message the user-facing explanation.
     */
    public PandaException(String message) {
        super(message);
    }
}
