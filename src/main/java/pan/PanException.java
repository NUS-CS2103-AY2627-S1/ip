package pan;

/**
 * Signals an error caused by invalid user input, carrying a friendly,
 * PanPan-voiced message that is shown directly to the user.
 */
public class PanException extends Exception {
    /** Creates an exception carrying a user-facing, PanPan-voiced message. */
    public PanException(String message) {
        super(message);
    }
}
