package ayre.exceptions;

/**
 * Thrown when user does not input the correct number of arguments.
 */
public class WrongNumberOfArgumentsException extends InvalidCommandException {
    /**
     * Initializes the exception by passing the message on.
     *
     * @param e Error message.
     */
    public WrongNumberOfArgumentsException(String e) {
        super(e);
    }
}
