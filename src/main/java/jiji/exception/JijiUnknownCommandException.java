package jiji.exception;

/**
 * Exception thrown when a user enters an unrecognized or invalid command keyword.
 */
public class JijiUnknownCommandException extends JijiException {

    /**
     * Constructs a JijiUnknownCommandException with the standard unrecognized command message.
     */
    public JijiUnknownCommandException() {
        super("OOPS! ₍^› ꘍ ‹ ^₎⟆ I'm sorry, but I don't know what that means.");
    }
}
