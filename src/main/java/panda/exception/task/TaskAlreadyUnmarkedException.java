package panda.exception.task;

import panda.exception.ApplicationException;
import panda.task.Task;

/**
 * Signals an attempt to unmark a task that is already unmarked.
 */
public class TaskAlreadyUnmarkedException extends ApplicationException {

    /**
     * Creates an exception that identifies the task that was unmarked twice.
     *
     * @param task Unmarked task the user tried to unmark again.
     */
    public TaskAlreadyUnmarkedException(Task task) {
        super("You can't undo what you never did. This task is still waiting:"
                + System.lineSeparator() + "  " + task + System.lineSeparator()
                + "Creative escape attempt, though.");
    }
}
