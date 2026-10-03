package ramly.exception;

/** Indicates that task data could not be read or written safely. */
public class StorageException extends RuntimeException {
    /** Creates a storage exception with its underlying cause. */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Creates a storage exception without an underlying cause. */
    public StorageException(String message) {
        super(message);
    }
}
