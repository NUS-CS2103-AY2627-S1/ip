package snoopy.exception;

/**
 * Represents an input error that Snoopy can explain to the user.
 */
public class SnoopyException extends Exception {
    /**
     * Creates a Snoopy-specific exception with a user-friendly message.
     *
     * @param message Explanation of the invalid input.
     */
    public SnoopyException(String message) {
        super(message);
    }
}
