package et.command;

import et.exception.ETException;
import et.task.Task;
import et.task.TaskList;

/**
 * Provides a common task reference for commands that update one existing task.
 */
public abstract class TaskIndexCommand extends Command {
    /** The zero-based index of the task affected by this command. */
    protected final int taskIndex;

    /**
     * Creates a command that affects one task.
     *
     * @param taskIndex the zero-based index of the affected task
     */
    protected TaskIndexCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Returns the referenced task after confirming that its index is in the current list.
     *
     * @param tasks ET's current task list
     * @return the referenced task
     * @throws ETException if the task number is not in the current list
     */
    protected Task getTask(TaskList tasks) throws ETException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new ETException("My scanner cannot find that task number in the current list.");
        }
        return tasks.getTask(taskIndex);
    }
}
