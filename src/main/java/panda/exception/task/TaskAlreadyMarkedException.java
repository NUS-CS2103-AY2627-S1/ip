package panda.exception.task;

import panda.exception.ApplicationException;
import panda.task.Task;

/**
 * Signals an attempt to mark a task that has already been marked.
 */
public class TaskAlreadyMarkedException extends ApplicationException {

    /**
     * Creates an exception that identifies the task that was marked twice.
     *
     * @param task Marked task the user tried to mark again.
     */
    public TaskAlreadyMarkedException(Task task) {
        super("You already finished this. Is memory training next?"
                + System.lineSeparator() + "  " + task + System.lineSeparator()
                + "One victory lap was plenty.");
    }
}
