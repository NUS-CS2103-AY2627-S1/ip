package luna;

/**
 * Represents an application-specific exception raised by Luna.
 */
public class LunaException extends Exception {
    /**
     * Creates a Luna exception with the given user-facing message.
     *
     * @param message Explanation of the problem.
     */
    public LunaException(String message) {
        super(message);
    }
}
