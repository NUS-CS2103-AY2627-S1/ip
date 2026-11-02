package megia.exception;

/** Reports malformed or unreadable task data in the local storage file. */
public class StorageException extends MegiaException {
    /** Path of the storage file associated with the error. */
    private final String path;
    /** One-based malformed line number, or zero when the file could not be read. */
    private final int lineNumber;

    /**
     * Creates a storage error for a particular file line or file-access failure.
     *
     * @param path Storage file path.
     * @param lineNumber One-based line number containing invalid data, or zero for an access failure.
     */
    public StorageException(String path, int lineNumber) {
        this(lineNumber == 0 ? ErrorCode.STORAGE_UNREADABLE : ErrorCode.STORAGE_MALFORMED,
                path, lineNumber);
    }

    private StorageException(ErrorCode errorCode, String path, int lineNumber) {
        super(errorCode, path, lineNumber);
        this.path = path;
        this.lineNumber = lineNumber;
    }

    /**
     * Creates an error for a task file that could not be saved.
     *
     * @param path Storage file path.
     * @return Storage write error for the path.
     */
    public static StorageException createWriteFailure(String path) {
        return new StorageException(ErrorCode.STORAGE_WRITE_FAILED, path, 0);
    }

    /**
     * Returns the storage file path associated with this error.
     *
     * @return Storage file path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the one-based malformed line number, or zero for a file access error.
     *
     * @return Malformed line number.
     */
    public int getLineNumber() {
        return lineNumber;
    }
}
