package samantha.exception;

/**
 * Indicates that the user entered a command word Samantha does not recognize.
 */
public class UnknownCommandException extends InputException {
    /**
     * User-facing reply for an unrecognized command.
     */
    public static final String MESSAGE =
            "I don't recognize that. Type help if you want the guide.";

    /**
     * Creates an exception for an unrecognized command word.
     */
    public UnknownCommandException() {
        super(MESSAGE);
    }
}
