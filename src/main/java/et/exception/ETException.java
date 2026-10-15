package et.exception;

/**
 * Represents an error caused by an invalid command entered for ET.
 */
public class ETException extends Exception {
    /** Identifies this version of the exception class for Java serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an ET-specific exception with a message for the user.
     *
     * @param message an explanation of how the command should be corrected
     */
    public ETException(String message) {
        super(message);
    }
}
