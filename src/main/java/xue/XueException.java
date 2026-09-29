package xue;

/**
 * Represents an input error that Xue can explain to the user.
 */
public class XueException extends RuntimeException {
    /**
     * Creates an input error with the given explanation.
     *
     * @param message the user-facing explanation
     */
    public XueException(String message) {
        super(message);
    }
}
