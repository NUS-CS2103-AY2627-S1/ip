package mira.exception;

/**
 * Represents invalid user input that Mira can explain and recover from.
 */
public class MiraException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message explanation displayed to the user.
     */
    public MiraException(String message) {
        super(message);
    }
}
