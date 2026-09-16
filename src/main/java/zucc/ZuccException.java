package zucc;

/**
 * Signals that Zucc cannot carry out a command because the user's input is invalid.
 * Keeping expected input errors separate from programming errors lets the chatbot
 * explain the problem and continue accepting commands safely.
 */
public class ZuccException extends Exception {
    /** Serialization version for this exception type. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an input error with a message that can be shown directly to the user.
     *
     * @param message explanation of how the user can correct the command.
     */
    public ZuccException(String message) {
        super(message);
    }

    /**
     * Creates an input or storage error caused by another failure.
     *
     * @param message explanation that can be shown directly to the user.
     * @param cause underlying failure.
     */
    public ZuccException(String message, Throwable cause) {
        super(message, cause);
    }
}
