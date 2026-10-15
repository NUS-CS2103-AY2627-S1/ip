package greenchonk.exception;

import java.io.Serial;

/**
 * Represents a failure to load, decode, encode, or save task data.
 */
public class StorageException extends GreenChonkException {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates a storage exception with an explanation that can be shown to the user.
     *
     * @param message the user-facing explanation of the error.
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Creates a storage exception with a user-facing explanation and underlying cause.
     *
     * @param message the user-facing explanation of the error.
     * @param cause the error that caused this exception.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
