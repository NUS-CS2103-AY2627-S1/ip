package tem;

/**
 * Represents an input error that can be shown to the Tem user.
 */
public class TemException extends Exception {

    /**
     * Creates an exception with a user-facing explanation of the input error.
     *
     * @param message explanation of how the input should be corrected.
     */
    public TemException(String message) {
        super(message);
    }
}
