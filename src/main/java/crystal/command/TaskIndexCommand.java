package crystal.command;

import crystal.exception.CrystalException;
import crystal.task.Task;
import crystal.task.TaskList;

/**
 * Base command for operations targeting one task-list index.
 */
public abstract class TaskIndexCommand extends Command {
    /** Zero-based index of the target task. */
    protected final int taskIndex;

    /**
     * Creates a command targeting a zero-based task index.
     *
     * @param taskIndex zero-based task index.
     */
    protected TaskIndexCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Returns the target task after validating that it still exists.
     *
     * @param tasks current task list.
     * @return target task.
     * @throws CrystalException if the index is outside the current task list.
     */
    protected Task getTask(TaskList tasks) throws CrystalException {
        if (taskIndex < 0 || taskIndex >= tasks.getTaskCount()) {
            throw new CrystalException("That task number does not exist!");
        }
        return tasks.getTask(taskIndex);
    }
}
