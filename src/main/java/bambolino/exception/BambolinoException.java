package bambolino.exception;

/**
 * Represents an error caused by an invalid Bambolino command.
 */
public class BambolinoException extends Exception {
    /**
     * Creates an exception with a message that explains how to correct the command.
     *
     * @param message The error explanation shown to the user.
     */
    public BambolinoException(String message) {
        super(message);
    }
}
