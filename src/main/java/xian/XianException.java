package xian;

/**
 * Represents an exception specific to the Xian application,
 * thrown when a user command or saved data cannot be processed correctly.
 */
public class XianException extends Exception {

    /**
     * Creates a new XianException with the given error message.
     *
     * @param message the message describing the error.
     */
    public XianException(String message) {
        super(message);
    }
}
