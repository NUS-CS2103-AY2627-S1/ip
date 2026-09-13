package xuan.exception;

/**
 * Represents an exception specific to the Xuan chatbot.
 */
public class XuanException extends Exception {

    /**
     * Creates a XuanException with the specified error message.
     *
     * @param message the error message
     */
    public XuanException(String message) {
        super(message);
    }
}
