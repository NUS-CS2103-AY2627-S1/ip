package samantha.exception;

/**
 * Indicates that the current task list could not be saved.
 */
public class TaskFileWriteException extends StorageException {
    /**
     * Creates an exception for a failed task-file write.
     *
     * @param cause underlying file-system failure
     */
    public TaskFileWriteException(Throwable cause) {
        super("I couldn't save your tasks to disk.", cause);
    }
}
