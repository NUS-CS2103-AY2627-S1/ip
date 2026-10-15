package noms.exception;

/**
 * Indicates that an event command is missing or has invalid event details.
 */
public class InvalidEventException extends NomsException {
    /**
     * Creates the exception for a malformed event command.
     *
     * @param usage the correct event command format to show the user
     */
    public InvalidEventException(String usage) {
        super("This event recipe needs more ingredients.\nTry: " + usage);
    }
}
