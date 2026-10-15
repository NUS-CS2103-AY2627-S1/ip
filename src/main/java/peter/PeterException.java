package peter;

/**
 * Represents an error specific to Peter's command handling, such as invalid
 * command syntax, an out-of-range task index, a malformed date, or a
 * failure to load saved tasks.
 */
public class PeterException extends Exception {
    /**
     * Creates a new PeterException with the given message.
     *
     * @param message the Peter-voiced error message to show the user
     */
    public PeterException(String message) {
        super(message);
    }
}
