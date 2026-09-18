package anby;

/**
 * Represents an error that can be shown to the user by Anby.
 */
public class AnbyException extends Exception {
    /**
     * Creates an exception with the given user-facing message.
     *
     * @param message error message to show to the user
     */
    public AnbyException(String message) {
        super(message);
    }
}
