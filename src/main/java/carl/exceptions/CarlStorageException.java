package carl.exceptions;

/**
 * Represents a failure to persist task data to local storage.
 */
public class CarlStorageException extends CarlException {

    /**
     * Constructs a storage exception with a user-facing explanation and original cause.
     *
     * @param message explanation of the storage failure.
     * @param cause underlying file-system exception.
     */
    public CarlStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
