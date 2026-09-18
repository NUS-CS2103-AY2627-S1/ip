package daddy.command;

import daddy.exception.DaddyException;
import daddy.task.TaskList;

/**
 * Provides shared task-index validation for commands that act on one task.
 */
public abstract class TaskIndexCommand extends Command {
    /** Stores the zero-based index of the task targeted by this command. */
    private final int taskIndex;

    /**
     * Stores the zero-based index of the task targeted by this command.
     *
     * @param taskIndex the task index to act on
     */
    protected TaskIndexCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Returns this command's zero-based task index.
     *
     * @return the task index
     */
    protected int getTaskIndex() {
        return taskIndex;
    }

    /**
     * Ensures this command's task index exists in the current list.
     *
     * @param tasks the task list to inspect
     * @throws DaddyException if the index is out of range
     */
    protected void ensureTaskExists(TaskList tasks) throws DaddyException {
        if (!tasks.hasTaskAt(taskIndex)) {
            if (tasks.size() == 0) {
                throw new DaddyException("Your task list is empty, my friend. Add a task first.");
            }
            throw new DaddyException(
                    "That task number is out of range, my friend. Pick a number from 1 to "
                            + tasks.size() + ".");
        }
    }
}
