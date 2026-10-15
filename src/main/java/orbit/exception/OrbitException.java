package orbit.exception;

/**
 * Represents an error caused by a command that Orbit cannot process.
 */
public class OrbitException extends Exception {
    /**
     * Creates an exception with an explanation that can be shown to the user.
     *
     * @param message explanation of the command error
     */
    public OrbitException(String message) {
        super(message);
    }
}
