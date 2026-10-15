package sage.command;

import sage.core.TaskList;
import sage.exception.SageException;
import sage.storage.Storage;
import sage.task.Task;
import sage.ui.Ui;

/**
 * Deletes a task from the list and saves the remaining tasks.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a delete command for a one-based task number.
     *
     * @param index the one-based position of the task to delete.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Removes the chosen task, persists the updated list, and shows confirmation.
     *
     * @param tasks the task list to modify.
     * @param ui the user interface used to display output.
     * @param storage the storage system used to persist the change.
     * @throws SageException If the task number is invalid or the save fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SageException {
        if (index <= 0 || index > tasks.size()) {
            throw new SageException("The task number is invalid. Use a number from the current list.");
        }
        Task removed = tasks.delete(index - 1);
        try {
            storage.save(tasks.getTasks());
        } catch (SageException exception) {
            tasks.getTasks().add(index - 1, removed);
            throw exception;
        }
        ui.showRemovedTask(removed, tasks.size());
    }
}
