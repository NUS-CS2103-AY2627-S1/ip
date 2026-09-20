package larper.exception;

/**
 * Signals that the user tried to unmark a task that is already not done.
 */
public class TaskAlreadyUnmarkedException extends LarperException {
    /**
     * Creates an exception for unmarking an already unmarked task.
     */
    public TaskAlreadyUnmarkedException() {
        super(" This task is already unmarked. Productivity allegations were never confirmed.");
    }
}
