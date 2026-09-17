package lebron.task;

import lebron.exception.LeBronException;

/**
 * Represents an exception thrown when a TaskList operation cannot be carried out,
 * e.g. accessing a task number that is out of range.
 */
public class TaskListException extends LeBronException {
    /**
     * Constructs a TaskListException with the given message.
     *
     * @param message Message explaining what went wrong.
     */
    public TaskListException(String message) {
        super(message);
    }
}
