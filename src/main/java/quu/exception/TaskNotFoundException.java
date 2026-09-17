package quu.exception;

/**
 * Thrown when a task number is well formed but no task sits at that position.
 */
public class TaskNotFoundException extends QuuException {

    /**
     * Creates the exception, naming the position that held no task and the range that
     * would have worked.
     *
     * @param index the one-based position the user asked for
     * @param size the number of tasks the list holds
     */
    public TaskNotFoundException(int index, int size) {
        super(buildMessage(index, size));
    }

    /**
     * Returns the message describing the failure, which depends on whether the list has
     * any tasks to offer as an alternative.
     *
     * @param index the one-based position the user asked for
     * @param size the number of tasks the list holds
     * @return the message to show the user
     */
    private static String buildMessage(int index, int size) {
        if (size == 0) {
            return String.format("There's no task %d. Your list is empty, so add a task first.", index);
        }
        return String.format("There's no task %d. Your list has %d task%s, numbered 1 to %d.",
                index, size, size == 1 ? "" : "s", size);
    }
}
