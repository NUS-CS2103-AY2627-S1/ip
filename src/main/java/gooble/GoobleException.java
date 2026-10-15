package gooble;

/**
 * Represents an error caused by an invalid command entered in Gooble.
 */
public class GoobleException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message explanation of the invalid command
     */
    public GoobleException(String message) {
        super(message);
    }
}
