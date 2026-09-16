package walter.command;

import walter.DukeException;
import walter.storage.Storage;
import walter.task.Task;
import walter.task.TaskList;
import walter.ui.Ui;

/**
 * Deletes one task and persists the changed task list.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a delete command for a zero-based task index.
     *
     * @param taskIndex Zero-based index of the task to delete.
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.DELETE;
    }

    /**
     * Deletes the selected task, displays it, and persists the shortened task list.
     *
     * @param tasks Task list from which the task is deleted.
     * @param ui User interface used to display the deleted task.
     * @param storage Storage used to persist the updated task list.
     * @throws DukeException If the task index is invalid or the updated list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        Task task = tasks.delete(taskIndex);
        try {
            storage.save(tasks.getTasks());
        } catch (DukeException exception) {
            tasks.restoreDeletedTask(taskIndex, task);
            throw exception;
        }
        ui.showDeletedTask(task, tasks.size());
    }
}
