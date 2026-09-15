package kiwi.command;

import kiwi.KiwiException;
import kiwi.Storage;
import kiwi.Ui;
import kiwi.task.Task;
import kiwi.task.TaskList;

/**
 * Deletes the task at a given 0-based index and saves.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a command that deletes the task at the given index.
     *
     * @param index 0-based index of the task to delete.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Deletes the task, saves the list, and shows a confirmation.
     *
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KiwiException {
        requireValidIndex(tasks, index);
        assert tasks.isValidIndex(index) : "Index is in range after the user-input check";
        Task removed = tasks.delete(index);
        saveOrRollback(tasks, storage, () -> tasks.add(index, removed));
        ui.showTaskDeleted(removed, tasks.size());
    }
}
