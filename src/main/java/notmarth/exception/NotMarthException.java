package notmarth.exception;

/**
 * Represents an error caused by an invalid command entered in NotMarth.
 *
 * <p>The chatbot catches this exception at its command-processing boundary so
 * that one invalid command does not terminate the session.</p>
 */
public class NotMarthException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a message intended for the user.
     *
     * @param message an explanation of what went wrong and, where useful, how
     *                to correct it.
     */
    public NotMarthException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a user-facing message and the underlying cause.
     *
     * @param message an explanation of what went wrong.
     * @param cause the exception that caused this error.
     */
    public NotMarthException(String message, Throwable cause) {
        super(message, cause);
    }
}
