package sage.command;

import sage.core.TaskList;
import sage.exception.SageException;
import sage.storage.Storage;
import sage.task.Task;
import sage.ui.Ui;

/**
 * Adds a new task to the list and persists the updated collection.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command to add a specific task.
     *
     * @param task the task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task to the list, saves it, and shows confirmation.
     *
     * @param tasks the task list to update.
     * @param ui the user interface used to display the result.
     * @param storage the storage system used to persist the update.
     * @throws SageException If the item is a duplicate, the list is full, or the save fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SageException {
        try {
            tasks.add(task);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            throw new SageException(exception.getMessage(), exception);
        }
        try {
            storage.save(tasks.getTasks());
        } catch (SageException exception) {
            tasks.delete(tasks.size() - 1);
            throw exception;
        }
        ui.showAddedTask(task, tasks.size());
    }
}
