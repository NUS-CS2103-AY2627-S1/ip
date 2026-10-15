package waddles;

/**
 * Represents an exception specific to Waddles, thrown when the user's
 * input cannot be understood or is otherwise invalid.
 */
public class WaddlesException extends Exception {

    /**
     * Prefix every WaddlesException message starts with. Kept as a shared
     * constant (rather than repeating the literal in every message) so the
     * GUI can reliably detect an error reply and highlight it, without that
     * detection silently breaking if the wording changes later.
     */
    public static final String ERROR_PREFIX = "OOPS!!!";

    /**
     * Creates a WaddlesException with the given error message.
     *
     * @param message Message describing what went wrong.
     */
    public WaddlesException(String message) {
        super(message);
    }
}
