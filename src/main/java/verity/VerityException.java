package verity;

/**
 * Signals that the user's input could not be understood or acted on.
 * Thrown by command parsing/validation logic and caught once in the
 * main loop, where its message is shown to the user directly.
 */
public class VerityException extends Exception {

    /**
     * Creates an exception with the given user-facing message.
     *
     * @param message Message explaining what went wrong, shown to the user.
     */
    public VerityException(String message) {
        super(message);
    }
}
