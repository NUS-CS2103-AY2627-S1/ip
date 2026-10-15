package benbot;

/** Indicates that a command is unknown or does not follow its required format. */
public class InvalidCommandException extends BenBotException {
    /** The version identifier used when this exception is serialized. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with instructions for correcting the command.
     *
     * @param message the instructions that describe the command error.
     */
    public InvalidCommandException(String message) {
        super(message);
    }
}
