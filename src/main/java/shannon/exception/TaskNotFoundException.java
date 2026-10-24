package shannon.exception;

/**
 * Thrown when a task number is a valid number but does not refer to a task in the list,
 * e.g. {@code mark 99} when there are only 3 tasks.
 */
public class TaskNotFoundException extends ShannonException {

    /**
     * Builds the message for a task number that is out of range.
     *
     * @param taskNumber the one-indexed number the user asked for
     * @param taskCount  how many tasks are actually in the list
     */
    public TaskNotFoundException(int taskNumber, int taskCount) {
        super(taskCount == 0
                ? "I don't have a task numbered " + taskNumber + " because your list is empty!"
                : "I don't have a task numbered " + taskNumber
                        + "! Pick a number from 1 to " + taskCount + ".");
    }
}
