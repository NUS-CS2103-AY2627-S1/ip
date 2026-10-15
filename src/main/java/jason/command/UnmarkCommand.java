package jason.command;

import jason.exception.InvalidIndexException;
import jason.storage.Storage;
import jason.task.TaskList;
import jason.ui.Ui;

/**
 * Marks one task as incomplete and persists the updated list.
 */
public class UnmarkCommand extends Command {
    private final int index;

    /**
     * Creates an unmark command for a one-based task index.
     *
     * @param index one-based index of the task to mark incomplete.
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    /**
     * Marks the selected task incomplete, saves the list, and reports the change.
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
        tasks.get(index).markIncomplete();
        storage.save(tasks);
        ui.showMarkedIncomplete(tasks.get(index));
    }
}
