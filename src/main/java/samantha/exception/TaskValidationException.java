package samantha.exception;

/**
 * Indicates that task data does not satisfy the task model's requirements.
 */
public class TaskValidationException extends SamanthaException {
    /**
     * Creates a task-validation exception with the supplied user-facing message.
     *
     * @param message explanation of the invalid task data
     */
    public TaskValidationException(String message) {
        super(message);
    }
}
