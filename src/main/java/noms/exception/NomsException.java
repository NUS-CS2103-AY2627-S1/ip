package noms.exception;

/**
 * Represents an input error in Noms.
 */
public class NomsException extends Exception {
    /**
     * Creates an exception carrying the given user-facing error message.
     *
     * @param message the explanation shown to the user
     */
    public NomsException(String message) {
        super(message);
    }
}
