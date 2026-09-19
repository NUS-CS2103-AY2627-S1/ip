package carl.exceptions;

/**
 * Represents an exception thrown when the user inputs a command that is not recognized.
 */
public class CarlUnknownCommandException extends CarlException {

    /**
     * Constructs a new {@code CarlUnknownCommandException} with a default error message
     * indicating the command is unknown.
     */
    public CarlUnknownCommandException() {
        super("That command didn’t land. Try `help` for the available controls.");
    }
}
