package penguin.command;

import penguin.exception.PenguinException;
import penguin.storage.Storage;
import penguin.task.Task;
import penguin.task.TaskList;
import penguin.task.TaskListSnapshot;
import penguin.ui.Ui;

/**
 * Represents a command that marks a task as completed.
 */
public class MarkCommand extends Command {
    /** Zero-based task index to mark. */
    private final int index;

    /**
     * Creates a mark command.
     *
     * @param taskIndex zero-based task index.
     */
    public MarkCommand(int taskIndex) {
        this.index = taskIndex;
    }

    /**
     * Marks the selected task as completed and persists the updated list.
     *
     * @param tasks task list to modify.
     * @param ui interface used for output.
     * @param storage storage used to persist the change.
     * @throws PenguinException if the index is invalid, the task is already
     *                          marked, or persistence fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PenguinException {
        assert tasks != null : "Mark command requires a task list";
        assert ui != null : "Mark command requires a user interface";
        assert storage != null : "Mark command requires storage";

        try {
            TaskListSnapshot previousState = tasks.createSnapshot();
            Task task = tasks.markTask(index);
            persistOrRestore(tasks, storage, previousState);
            ui.showMessage("Flipper-five! The following task has been marked.\n" + task);
        } catch (IndexOutOfBoundsException e) {
            throw new PenguinException("Invalid task index!");
        } catch (IllegalStateException e) {
            throw new PenguinException(e.getMessage());
        }
    }
}
