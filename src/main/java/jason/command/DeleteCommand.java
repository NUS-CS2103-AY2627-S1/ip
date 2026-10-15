package jason.command;

import jason.exception.InvalidIndexException;
import jason.storage.Storage;
import jason.task.Task;
import jason.task.TaskList;
import jason.ui.Ui;

/**
 * Deletes one task and persists the updated list.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a delete command for a one-based task index.
     *
     * @param index one-based index of the task to delete.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Deletes the selected task, saves the list, and reports the deletion.
     *
     * @param tasks task list to update.
     * @param ui user interface used to report the result.
     * @param storage storage used to persist the updated list.
     * @throws InvalidIndexException if the index does not identify a task.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws InvalidIndexException {
        if (!tasks.isValidIndex(index)) {
            throw new InvalidIndexException();
        }
        Task deletedTask = tasks.get(index);
        tasks.remove(index);
        storage.save(tasks);
        ui.showDeletedTask(deletedTask, tasks.size());
    }
}
