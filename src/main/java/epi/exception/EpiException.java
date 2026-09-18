package epi.exception;

/** Represents an expected, user-facing error in Epi. */
public class EpiException extends Exception {
    /**
     * Creates an exception with the message shown to the user.
     *
     * @param message Explanation of the error suitable for display in either user interface.
     */
    public EpiException(String message) {
        super(message);
    }

    /**
     * Preserves the technical cause while supplying a safe, actionable user message.
     *
     * @param message Explanation of the error suitable for display in either user interface.
     * @param cause Underlying failure, or {@code null} if its cause is unknown.
     */
    public EpiException(String message, Throwable cause) {
        super(message, cause);
    }
}
