package penny.command;

import penny.common.PennyException;
import penny.storage.Storage;
import penny.task.Task;
import penny.task.TaskList;
import penny.ui.Ui;

/**
 * Represents a command to delete a task from the task list by its 0-based index.
 */
public class DeleteCommand extends TaskIndexCommand {

    /**
     * Constructs a DeleteCommand with the target task index.
     *
     * @param index The 0-based index of the task to delete.
     */
    public DeleteCommand(int index) {
        super(index);
    }

    /**
     * Executes the task deletion, displays feedback, and updates storage.
     *
     * @param tasks The task list from which the task is removed.
     * @param ui The user interface used to display feedback.
     * @param storage The storage handler used to persist the updated task list.
     * @return The confirmation message from the user interface.
     * @throws PennyException If the index is invalid or an error occurs during saving.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws PennyException {
        assert tasks != null : "TaskList must not be null";
        assert ui != null : "Ui must not be null";
        assert storage != null : "Storage must not be null";
        assert this.index >= 0 : "Task index must be non-negative";

        Task removedTask = tasks.delete(this.index);
        storage.save(tasks);
        return ui.showTaskDeleted(removedTask, tasks.size());
    }
}
