package penguin.command;

import penguin.exception.PenguinException;
import penguin.storage.Storage;
import penguin.task.Task;
import penguin.task.TaskList;
import penguin.task.TaskListSnapshot;
import penguin.ui.Ui;

/**
 * Represents a command that deletes a task.
 */
public class DeleteCommand extends Command {
    /** Zero-based task index to delete. */
    private final int index;

    /**
     * Creates a delete command.
     *
     * @param taskIndex zero-based task index.
     */
    public DeleteCommand(int taskIndex) {
        this.index = taskIndex;
    }

    /**
     * Deletes the selected task, persists the updated list, and reports the result.
     *
     * @param tasks task list to modify.
     * @param ui interface used for output.
     * @param storage storage used to persist the change.
     * @throws PenguinException if the index is invalid or persistence fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PenguinException {
        assert tasks != null : "Delete command requires a task list";
        assert ui != null : "Delete command requires a ui";
        assert storage != null : "Delete command requires a storage";

        try {
            TaskListSnapshot previousState = tasks.createSnapshot();
            Task task = tasks.deleteTask(index);
            persistOrRestore(tasks, storage, previousState);
            ui.showMessage("Splash! I have removed '" + task
                    + "' from your list of tasks. Now you have "
                    + formatTaskCount(tasks.size()) + " in the list.");
        } catch (IndexOutOfBoundsException e) {
            throw new PenguinException("Invalid task index!");
        }
    }
}
