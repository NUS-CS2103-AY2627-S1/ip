package tianyi.storage;

import tianyi.TianyiException;

/**
 * Represents an error encountered while loading or saving tasks.
 */
public class StorageException extends TianyiException {
    /**
     * Creates an exception describing a task storage failure.
     *
     * @param message Explanation of the storage failure.
     */
    public StorageException(String message) {
        super(message);
    }
}
