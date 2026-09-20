package bkxss;

/**
 * Represents a recoverable command or persistence error reported to the user.
 */
public class BkxssException extends Exception {
    /**
     * Creates an exception with a message that explains how to correct the command.
     *
     * @param message explanation of the invalid command
     */
    public BkxssException(String message) {
        super(message);
    }
}
