package kibo.exception;

/**
 * Represents a problem saving or loading Kibo's task list on the hard disk.
 */
public class StorageException extends KiboException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a storage exception with a user-facing message.
     *
     * @param message explanation of the storage problem.
     */
    public StorageException(String message) {
        super(message);
    }
}
