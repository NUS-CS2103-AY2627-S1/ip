package baymax.exception;

/**
 * Represents a command that Baymax does not recognize.
 */
public class InvalidCommandException extends BaymaxException {

    /**
     * Creates an exception for an unsupported command.
     */
    public InvalidCommandException() {
        super("     OOPS!!! I'm sorry, but I don't know what that means :-(");
    }
}
