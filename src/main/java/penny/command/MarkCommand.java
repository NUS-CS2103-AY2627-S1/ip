package penny.command;

import penny.common.PennyException;
import penny.storage.Storage;
import penny.task.Task;
import penny.task.TaskList;
import penny.ui.Ui;

/**
 * Represents a command to mark a task in the task list as completed.
 */
public class MarkCommand extends TaskIndexCommand {

    /**
     * Constructs a MarkCommand with the target task index.
     *
     * @param index The 0-based index of the task to mark done.
     */
    public MarkCommand(int index) {
        super(index);
    }

    /**
     * Executes the task marking, displays confirmation, and updates storage.
     *
     * @param tasks The task list containing the task to mark.
     * @param ui The user interface used to show confirmation.
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

        Task task = tasks.mark(this.index);
        storage.save(tasks);
        return ui.showTaskMarked(task);
    }
}
