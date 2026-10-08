package rudra.command;

import java.util.ArrayList;

import rudra.exception.RudraException;
import rudra.storage.Storage;
import rudra.task.Task;
import rudra.ui.Ui;

/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that deletes the task at the given zero-based index.
     *
     * @param taskIndex Task index to delete.
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Deletes the requested task and shows the updated task count.
     *
     * @param tasks Current task list.
     * @param ui UI helper used to show output.
     * @param storage Storage helper used to persist changes.
     * @throws RudraException If the task number is invalid or saving fails.
     */
    @Override
    public void execute(ArrayList<Task> tasks, Ui ui, Storage storage) throws RudraException {
        validateTaskIndex(tasks, this.taskIndex);
        Task removedTask = deleteTask(tasks, this.taskIndex, storage);
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
