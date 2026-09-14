package arthur.exceptions;

/**
 * Indicates that persistent task storage could not be updated.
 */
public class StorageException extends ArthurRuntimeException {
    public StorageException(Exception e) {
        super("Error while saving state: " + e.getMessage());
    }
}
