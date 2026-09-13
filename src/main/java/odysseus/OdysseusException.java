package odysseus;

/**
 * An exception for a user-correctable Odysseus command error.
 */
public class OdysseusException extends Exception {
    /**
     * Creates an exception with a message that explains how the traveler can correct the command.
     *
     * @param message the corrective message to display.
     */
    public OdysseusException(String message) {
        super(message);
    }
}
