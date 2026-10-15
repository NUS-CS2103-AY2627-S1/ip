package yourhand.exceptions;

/** Represents a command that does not follow YourHand's supported syntax. */
public class InvalidCommandException extends YourHandException {
    /** Creates an invalid-command exception with a user-facing correction. */
    public InvalidCommandException(String message) {
        super(message);
    }
}
