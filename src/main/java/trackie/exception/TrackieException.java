package trackie.exception;

/**
 * Represents an invalid command or input that Trackie can explain to the user.
 */
public class TrackieException extends Exception {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message explanation of the invalid input
     */
    public TrackieException(String message) {
        super(message);
    }
}
