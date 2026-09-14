package arthur.exceptions;

/**
 * Indicates that a command could not be created or executed.
 */
public class InvalidCommandException extends ArthurRuntimeException {
    public InvalidCommandException(String command) {
        super("An error occurred while executing the following command: " + command);
    }
}
