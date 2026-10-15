package avon.exception;

/**
 * Signals that Avon could not load or save its task data.
 */
public class StorageException extends AvonException {
    /**
     * Creates a storage error with a user-facing explanation.
     *
     * @param message the explanation of the storage failure.
     */
    public StorageException(String message) {
        super(message);
    }
}
