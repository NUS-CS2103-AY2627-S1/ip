package alpha;

/** Represents an error caused by an invalid Moss command. */
public class AlphaException extends Exception {
    private static final long serialVersionUID = 1L;

    /** Creates an exception with a message suitable for displaying to the user. */
    public AlphaException(String message) {
        super(message);
    }
}
