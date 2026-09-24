package murphy;

/**
 * Represents an error that Murphy can explain to the user.
 */
public class MurphyException extends Exception {
    /** Creates an error with the message Murphy should display. */
    public MurphyException(String message) {
        super(message);
    }
}
