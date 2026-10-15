package samantha.exception;

/**
 * Indicates that the saved task file could not be read.
 */
public class TaskFileReadException extends StorageException {
    /**
     * Creates an exception for a failed task-file read.
     *
     * @param cause underlying file-system failure
     */
    public TaskFileReadException(Throwable cause) {
        super("I couldn't read the saved tasks.", cause);
    }
}
