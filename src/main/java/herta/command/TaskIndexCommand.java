package herta.command;

import herta.exception.HertaException;
import herta.task.TaskList;

/**
 * Base class for commands that operate on one task selected by its list number.
 */
public abstract class TaskIndexCommand extends Command {
    private static final String INVALID_ACTIVE_TASK_ERROR =
            "No active task has that number. Check the list and try again.";
    private final int taskIndex;

    /**
     * Creates a command for a zero-based task index.
     *
     * @param taskIndex the zero-based index of the selected task.
     */
    protected TaskIndexCommand(int taskIndex) {
        if (taskIndex < 0) {
            throw new IllegalArgumentException("A task index cannot be negative.");
        }
        this.taskIndex = taskIndex;
    }

    /**
     * Validates the selected index against the current task list.
     *
     * @param tasks the task list containing the selected task.
     * @return the validated zero-based task index.
     * @throws HertaException if the selected task does not exist.
     */
    protected int getTaskIndex(TaskList tasks) throws HertaException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new HertaException(INVALID_ACTIVE_TASK_ERROR);
        }

        return taskIndex;
    }
}
