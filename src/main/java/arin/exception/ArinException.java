package arin.exception;

/**
 * Represents an error caused by an invalid command entered into Arin.
 */
public class ArinException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message Explanation of the invalid command.
     */
    public ArinException(String message) {
        super(message);
    }
}
