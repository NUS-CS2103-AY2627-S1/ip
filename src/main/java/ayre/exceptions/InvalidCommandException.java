package ayre.exceptions;

/**
 * Thrown when the user-input command is invalid in any way.
 * This should only occur due to the arguments being invalid.
 */
public class InvalidCommandException extends AyreException {
    /**
     * Initializes the exception by passing the message on.
     *
     * @param e Error message.
     */
    public InvalidCommandException(String e) {
        super(e);
    }
}
