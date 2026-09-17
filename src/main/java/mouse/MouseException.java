package mouse;

/**
 * A user-facing error from an invalid Mouse command.
 */
public class MouseException extends Exception {

    /**
     * Creates an exception with the given error message.
     *
     * @param message Message shown to the user.
     */
    public MouseException(String message) {
        super(message);
    }
}
