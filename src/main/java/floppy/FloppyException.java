package floppy;

/**
 * Represents an error caused by an invalid command or task operation.
 */
public class FloppyException extends Exception {
    /**
     * Creates an exception with a message suitable for display to the user.
     *
     * @param message Explanation of the error.
     */
    public FloppyException(String message) {
        super(message);
    }
}
