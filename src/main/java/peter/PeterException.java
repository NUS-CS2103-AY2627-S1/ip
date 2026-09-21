package peter;

/**
 * Represents an error that can be explained clearly to the user.
 */
public class PeterException extends Exception {
    /**
     * Creates an error whose message can be shown to the user as it is.
     *
     * @param message wording to display.
     */
    public PeterException(String message) {
        super(message);
    }

    /**
     * Creates an error that keeps the underlying failure for diagnosis while
     * still displaying friendly wording.
     *
     * <p>Used where a technical exception, such as a failed date parse or a
     * file error, would confuse the user if shown directly.
     *
     * @param message wording to display.
     * @param cause underlying failure being reported.
     */
    public PeterException(String message, Throwable cause) {
        super(message, cause);
    }
}
