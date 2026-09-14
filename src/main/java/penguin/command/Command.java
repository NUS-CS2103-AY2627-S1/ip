package penguin.command;

import penguin.exception.PenguinException;
import penguin.storage.Storage;
import penguin.task.TaskList;
import penguin.task.TaskListSnapshot;
import penguin.ui.Ui;

/**
 * Represents an executable Penguin command.
 */
public abstract class Command {
    /**
     * Creates a command.
     */
    protected Command() {
    }

    /**
     * Executes the command.
     *
     * @param tasks task list to modify or query.
     * @param ui interface used for output.
     * @param storage storage used to persist changes.
     * @throws PenguinException if execution fails.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws PenguinException;

    /**
     * Checks whether this command exits the application.
     *
     * @return true if this command exits Penguin.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Persists a task-list change and restores the previous state if saving fails.
     *
     * @param tasks changed task list.
     * @param storage storage used to persist the change.
     * @param previousState state to restore if persistence fails.
     * @throws PenguinException if persistence fails.
     */
    protected void persistOrRestore(TaskList tasks, Storage storage,
            TaskListSnapshot previousState) throws PenguinException {
        try {
            storage.saveTaskLines(tasks.toStorageLines());
        } catch (PenguinException e) {
            tasks.restoreSnapshot(previousState);
            throw e;
        }
    }

    /**
     * Formats a task count using the correct singular or plural noun.
     *
     * @param taskCount number of tasks.
     * @return human-readable task count.
     */
    protected String formatTaskCount(int taskCount) {
        return taskCount + (taskCount == 1 ? " task" : " tasks");
    }

}
