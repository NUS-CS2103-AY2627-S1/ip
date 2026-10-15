package pip.command;

import pip.exception.ParserException;
import pip.storage.Storage;
import pip.task.Task;
import pip.task.TaskList;
import pip.ui.Ui;


/**
 * Represents a command that removes a task from the task list by index.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a DeleteCommand that will remove the task at the given
     * zero-based index when executed.
     *
     * @param index The zero-based index of the task to remove.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Removes the task at this command's index, shows a confirmation to
     * the user, and persists the updated task list to storage.
     *
     * @param tasks The task list to remove the task from.
     * @param ui The UI used to confirm the removal.
     * @param storage The storage used to persist the updated task list.
     * @throws ParserException If the index is out of range for the task list.
     * @throws Exception If an I/O error occurs while saving.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws Exception {
        if (index < 0 || index >= tasks.size()) {
            throw new ParserException("Sorry! Invalid task index!");
        }
        Task removedTask = tasks.remove(index);
        ui.showTaskDeleted(removedTask, tasks.size());
        storage.save(tasks);
    }
}
