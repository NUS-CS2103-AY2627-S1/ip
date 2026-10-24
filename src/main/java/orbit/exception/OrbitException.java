package orbit.exception;

/**
 * Signals that Orbit cannot safely execute a user command.
 */
public class OrbitException extends Exception {
    /**
     * Creates an exception with an explanation suitable for the user.
     *
     * @param message explanation of the invalid command or argument
     */
    public OrbitException(String message) {
        super(message);
    }
}
