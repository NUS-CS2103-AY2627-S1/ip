package larper.exception;

/**
 * Signals that the user tried to mark a task that is already done.
 */
public class TaskAlreadyMarkedException extends LarperException {
    /**
     * Creates an exception for marking an already done task.
     */
    public TaskAlreadyMarkedException() {
        super(" This one is already done. No need to perform extra productivity.");
    }
}
