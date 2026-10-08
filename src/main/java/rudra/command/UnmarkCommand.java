package rudra.command;

import java.util.ArrayList;

import rudra.exception.RudraException;
import rudra.storage.Storage;
import rudra.task.Task;
import rudra.ui.Ui;

/**
 * Marks a task as not done.
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that unmarks the task at the given zero-based index.
     *
     * @param taskIndex Task index to unmark.
     */
    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Marks the requested task as not done and saves the updated task list.
     *
     * @param tasks Current task list.
     * @param ui UI helper used to show output.
     * @param storage Storage helper used to persist changes.
     * @throws RudraException If the task number is invalid or saving fails.
     */
    @Override
    public void execute(ArrayList<Task> tasks, Ui ui, Storage storage) throws RudraException {
        validateTaskIndex(tasks, this.taskIndex);
        updateTaskStatus(tasks, this.taskIndex, storage, false);
        ui.showTaskUnmarked(tasks.get(this.taskIndex));
    }
}
