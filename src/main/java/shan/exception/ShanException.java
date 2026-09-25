package shan.exception;

/**
 * Represents an error that Shan can report to the user.
 */
public class ShanException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an exception with a message suitable for displaying to the user.
     *
     * @param message Explanation of the error.
     */
    public ShanException(String message) {
        super(message);
    }
}
