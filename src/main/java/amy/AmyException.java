package amy;

/**
 * Represents an input error reported by Amy.
 */
public class AmyException extends Exception {
    /**
     * Creates an input error with the supplied user-facing message.
     *
     * @param message the explanation shown to the user
     */
    public AmyException(String message) {
        super(message);
    }
}
