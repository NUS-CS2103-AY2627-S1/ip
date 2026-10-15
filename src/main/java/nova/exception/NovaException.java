package nova.exception;

/**
 * Signals an invalid command or other input error in the Nova chatbot.
 */
public class NovaException extends Exception {
    /** Ensures serialized exceptions remain compatible across versions. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-facing error message.
     *
     * @param message the explanation of the input error
     */
    public NovaException(String message) {
        super(message);
    }
}
