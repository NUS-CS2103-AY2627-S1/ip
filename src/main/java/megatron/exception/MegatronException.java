package megatron.exception;

/**
 * Base class for input errors reported by the Megatron chatbot.
 */
public class MegatronException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message The user-facing error message.
     */
    public MegatronException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a user-facing message and its underlying cause.
     *
     * @param message The user-facing error message.
     * @param cause The error that caused this exception.
     */
    protected MegatronException(String message, Throwable cause) {
        super(message, cause);
    }
}
