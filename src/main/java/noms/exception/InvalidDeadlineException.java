package noms.exception;

/**
 * Indicates that a deadline command is missing or has invalid deadline details.
 */
public class InvalidDeadlineException extends NomsException {
    /**
     * Creates the exception for a malformed deadline command.
     *
     * @param usage the correct deadline command format to show the user
     */
    public InvalidDeadlineException(String usage) {
        super("This deadline recipe is incomplete.\nTry: " + usage);
    }
}
