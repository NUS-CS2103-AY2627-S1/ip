package toothless.storage;

/**
 * Represents an expected failure while reading or writing Toothless's data.
 */
public class StorageException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a storage error while preserving its file-system cause.
     *
     * @param message summary of the failed storage operation.
     * @param cause underlying file-system failure.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
