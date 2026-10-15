package potato.command;

import potato.exception.PotatoException;
import potato.storage.Storage;
import potato.task.Task;
import potato.tasklist.TaskList;
import potato.ui.Ui;

/**
 * Command to delete a task from the task list by its index.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Constructs a {@code DeleteCommand} targeting the specified task index.
     *
     * @param index Zero-based index of the task to delete.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Executes the task removal from the task list, updates file storage, and notifies the user.
     *
     * @param tasks Task list to modify.
     * @param ui User interface for displaying output notifications.
     * @param storage Persistence layer for saving updated task list.
     * @throws PotatoException If the target index is out of bounds or file saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PotatoException {
        Task removed = tasks.remove(index);
        storage.save(tasks.getTasks());
        ui.showTaskRemoved(removed, tasks.size());
    }
}