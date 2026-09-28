package kotha;

/** Represents an invalid command or task operation in Kotha. */
public class KothaException extends Exception {
    private static final long serialVersionUID = 1L;

    /** Creates an exception with the supplied user-facing message. */
    public KothaException(String message) {
        super(message);
    }
}
