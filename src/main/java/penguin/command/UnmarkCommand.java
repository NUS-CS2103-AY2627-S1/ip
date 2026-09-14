package penguin.command;

import penguin.exception.PenguinException;
import penguin.storage.Storage;
import penguin.task.Task;
import penguin.task.TaskList;
import penguin.task.TaskListSnapshot;
import penguin.ui.Ui;

/**
 * Represents a command that marks a task as incomplete.
 */
public class UnmarkCommand extends Command {
    /** Zero-based task index to unmark. */
    private final int index;

    /**
     * Creates an unmark command.
     *
     * @param taskIndex zero-based task index.
     */
    public UnmarkCommand(int taskIndex) {
        this.index = taskIndex;
    }

    /**
     * Marks the selected task as incomplete and persists the updated list.
     *
     * @param tasks task list to modify.
     * @param ui interface used for output.
     * @param storage storage used to persist the change.
     * @throws PenguinException if the index is invalid, the task is already
     *                          unmarked, or persistence fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PenguinException {
        assert tasks != null : "Unmark command requires a task list";
        assert ui != null : "Unmark command requires a user interface";
        assert storage != null : "Unmark command requires storage";

        try {
            TaskListSnapshot previousState = tasks.createSnapshot();
            Task task = tasks.unmarkTask(index);
            persistOrRestore(tasks, storage, previousState);
            ui.showMessage("No worries! The following task has been unmarked.\n" + task);
        } catch (IndexOutOfBoundsException e) {
            throw new PenguinException("Invalid task index!");
        } catch (IllegalStateException e) {
            throw new PenguinException(e.getMessage());
        }
    }
}
