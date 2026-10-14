package bingus.exception;

/**
 * Represents invalid user input or an application operation that cannot be completed.
 */
public class BingusException extends RuntimeException {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message explanation of the problem.
     */
    public BingusException(String message) {
        super(message);
    }
}
