package charlie.command;

import charlie.storage.Storage;
import charlie.task.Task;
import charlie.task.TaskList;
import charlie.ui.Ui;

/**
 * Deletes one task from the task list.
 */
public class DeleteCommand extends Command {
    /** Zero-based index of the task to delete. */
    private final int index;

    /**
     * Creates a command for the task at the given zero-based index.
     *
     * @param index Zero-based index of the task to delete.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Deletes the selected task, saves the task list, and displays the result.
     *
     * @param tasks Task collection containing the selected task.
     * @param ui User interface used to display the result.
     * @param storage Storage used to persist the updated task list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        storage.save(tasks.getTasksAfterDeleting(index));
        Task deletedTask = tasks.delete(index);
        ui.showMessages(
                "I've cleared this task from our trail:",
                "  " + deletedTask);
        ui.showTaskCount(tasks.getSize());
    }
}
