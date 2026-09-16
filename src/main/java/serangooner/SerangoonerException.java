package serangooner;

/**
 * Represents invalid input entered as a Serangooner command.
 */
public class SerangoonerException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an exception carrying the message shown to the user.
     *
     * @param message Explanation of what was wrong with the input.
     */
    public SerangoonerException(String message) {
        super(message);
    }

    /**
     * Constructs an exception carrying the message shown to the user and its cause.
     *
     * @param message Explanation of what was wrong with the input.
     * @param cause Underlying exception that triggered this one.
     */
    public SerangoonerException(String message, Throwable cause) {
        super(message, cause);
    }
}
