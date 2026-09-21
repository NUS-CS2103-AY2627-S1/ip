package sel.exception;

/**
 * Represents an error specific to Sel's operation, such as an invalid
 * command, a malformed argument, or a task index that does not exist.
 * Carries a user-facing message describing what went wrong.
 */
public class SelException extends Exception {

    /**
     * Creates a new SelException with the given user-facing message.
     *
     * @param message description of the error, shown to the user as-is.
     */
    public SelException(String message) {
        super(message);
    }
}
