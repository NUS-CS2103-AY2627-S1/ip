package shrekanddonkey.exception;

/**
 * Represents an application-specific input validation error.
 */
public class ShrekAndDonkeyException extends RuntimeException {
    /**
     * Creates an exception without a detail message.
     */
    public ShrekAndDonkeyException() {
        super();
    }

    /**
     * Creates an exception with a detail message.
     *
     * @param message explanation of the validation error.
     */
    public ShrekAndDonkeyException(String message) {
        super(message);
    }

}
