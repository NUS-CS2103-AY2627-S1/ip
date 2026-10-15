package olaf;

/**
 * Signals that Olaf could not load or save its task data safely.
 */
final class StorageException extends Exception {
    private static final long serialVersionUID = 1L;

    StorageException(String message) {
        super(message);
    }

    StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
