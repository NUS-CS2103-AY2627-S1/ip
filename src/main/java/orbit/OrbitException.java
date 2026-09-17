package orbit;

/**
 * Represents an invalid command or task operation entered by the user.
 */
public class OrbitException extends Exception {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message Explanation of the invalid operation.
     */
    public OrbitException(String message) {
        super(message);
    }
}
