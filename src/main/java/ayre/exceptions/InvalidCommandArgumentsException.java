package ayre.exceptions;

/**
 * Thrown when command arguments are detected to violate expected format or cause runtime exceptions to be thrown.
 */
public class InvalidCommandArgumentsException extends InvalidCommandException {
    /**
     * Initializes the exception by passing the message on.
     *
     * @param e Error message.
     */
    public InvalidCommandArgumentsException(String e) {
        super(e);
    }
}
