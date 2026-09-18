package korvus.storage;

/**
 * Exception for Storage conflict issues.
 */
public class StorageConflictException extends Exception {

    /**
     * Returns an instance of StorageConflictException, if there is any conflicts with file access in Storage.
     *
     * @param msg Message to be passed down.
     */
    public StorageConflictException(String msg) {
        super(msg);
    }
}
