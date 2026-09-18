package alpha.storage;

/** Indicates that Moss could not read or write its task storage file. */
public class StorageException extends Exception {
    private static final long serialVersionUID = 1L;

    /** Creates an exception describing a storage failure and its underlying cause. */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
