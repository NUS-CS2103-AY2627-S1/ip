package percy;

/**
 * Represents an error that Percy itself raises, such as an unknown command
 * or a badly formed argument.
 *
 * <p>Having a dedicated exception type lets the main loop catch Percy's own
 * expected errors (and show a friendly message) separately from unexpected
 * bugs, which are allowed to surface normally.
 */
public class PercyException extends Exception {
    /**
     * Creates the exception with a message suitable for showing to the user.
     *
     * @param message the user-facing explanation of what went wrong
     */
    public PercyException(String message) {
        super(message);
    }
}
