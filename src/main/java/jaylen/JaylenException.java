package jaylen;

/** Represents an input error that can be explained to a Jaylen user. */
public class JaylenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an input error with a user-facing explanation.
     *
     * @param message the explanation shown to the user
     */
    public JaylenException(String message) {
        super(message);
    }
}
