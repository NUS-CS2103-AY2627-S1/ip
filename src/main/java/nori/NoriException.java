package nori;

/**
 * Represents an invalid command or argument supplied to Nori.
 */
public class NoriException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message Explanation of the invalid operation.
     */
    public NoriException(String message) {
        super(message);
    }
}
