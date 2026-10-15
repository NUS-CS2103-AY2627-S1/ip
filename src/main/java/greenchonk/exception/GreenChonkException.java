package greenchonk.exception;

import java.io.Serial;

/**
 * Represents an invalid command or task operation specific to Green Chonk.
 */
public class GreenChonkException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with an explanation that can be shown to the user.
     *
     * @param message the user-facing explanation of the error.
     */
    public GreenChonkException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a user-facing explanation and underlying cause.
     *
     * @param message the user-facing explanation of the error.
     * @param cause the error that caused this exception.
     */
    public GreenChonkException(String message, Throwable cause) {
        super(message, cause);
    }
}
