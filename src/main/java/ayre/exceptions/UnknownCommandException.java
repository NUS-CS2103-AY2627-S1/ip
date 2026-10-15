package ayre.exceptions;

/**
 * Thrown when user input command does not match any known commands.
 */
public class UnknownCommandException extends AyreException {
    /**
     * Initializes the exception by passing the message on.
     *
     * @param e Error message.
     */
    public UnknownCommandException(String e) {
        super(e);
    }
}
