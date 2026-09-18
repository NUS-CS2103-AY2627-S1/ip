package kopi;

/**
 * Represents an error caused by an invalid command from the user.
 */
public class KopiException extends Exception {
    private static final long serialVersionUID = 1L;

    /** Creates an exception with a message suitable for the user. */
    public KopiException(String message) {
        super(message);
    }
}
