package samantha.exception;

/**
 * Base class for failures while reading, validating, or writing task data.
 */
public abstract class StorageException extends SamanthaException {
    /**
     * Creates a storage exception with a message and underlying cause.
     *
     * @param message explanation of the storage failure
     * @param cause lower-level failure that caused this exception
     */
    protected StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
