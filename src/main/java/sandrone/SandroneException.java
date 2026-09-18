package sandrone;

/**
 * Represents a problem caused by invalid chatbot input.
 */
public class SandroneException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception containing an explanation for the user.
     */
    public SandroneException(String message) {
        super(message);
    }
}
