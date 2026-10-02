package ari.exception;

/**
 * Signals that a command refers to a task ID that is not in the task list.
 */
public class TaskNotFoundException extends Exception {
    /**
     * Creates an exception for the specified invalid task ID.
     *
     * @param id Invalid task ID.
     */
    public TaskNotFoundException(int id) {
        super(String.format("Task %s does not exist!\nSend 'list' to see which tasks you have left!", id));
    }
}
