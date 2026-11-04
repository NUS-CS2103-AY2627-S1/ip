package soar.exception;

/**
 * Represents malformed task data or a persistence failure that Soar can explain.
 */
public class StorageException extends SoarException {
    /**
     * Creates a storage exception with a user-friendly explanation.
     *
     * @param message Explanation shown to the user.
     */
    public StorageException(String message) {
        super(message);
    }
}
