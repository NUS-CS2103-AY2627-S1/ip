package nyonbot.model;

/**
 * Represents a checked exception caused by invalid NyonBot command input.
 */
public class NyonException extends Exception {
    /**
     * Creates an exception with a user-facing message.
     *
     * @param message explanation of the invalid input.
     */
    public NyonException(String message) {
        super(message);
    }
}
