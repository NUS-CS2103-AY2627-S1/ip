package nori;

/**
 * Represents an input error that can be explained to the user and recovered from.
 */
public class NoriException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a user-facing Nori error.
     *
     * @param message explanation displayed to the user.
     */
    public NoriException(String message) {
        super(message);
    }
}
