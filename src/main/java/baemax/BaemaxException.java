package baemax;

/**
 * Represents an error caused by an invalid Baemax command.
 */
public class BaemaxException extends Exception {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message the explanation to show the user
     */
    public BaemaxException(String message) {
        super(message);
    }
}
