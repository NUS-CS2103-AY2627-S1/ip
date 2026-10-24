package shannon.exception;

/**
 * Thrown when a task being added has the same details as one already in the list,
 * e.g. {@code todo read book} typed twice.
 */
public class DuplicateTaskException extends ShannonException {

    /**
     * Builds the message pointing at the task that is already in the list.
     *
     * @param existingTask the task already in the list, as it is shown to the user.
     * @param taskNumber   its number in the list, counting from 1, so the user can find it.
     */
    public DuplicateTaskException(String existingTask, int taskNumber) {
        super("You already have that task, as number " + taskNumber + ":\n  " + existingTask);
    }
}
