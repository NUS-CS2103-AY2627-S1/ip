package rudra.command;

import java.util.ArrayList;

import rudra.exception.RudraException;
import rudra.storage.Storage;
import rudra.task.Task;
import rudra.ui.Ui;

/**
 * Marks a task as done.
 */
public class MarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that marks the task at the given zero-based index.
     *
     * @param taskIndex Task index to mark.
     */
    public MarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Marks the requested task as done and saves the updated task list.
     *
     * @param tasks Current task list.
     * @param ui UI helper used to show output.
     * @param storage Storage helper used to persist changes.
     * @throws RudraException If the task number is invalid or saving fails.
     */
    @Override
    public void execute(ArrayList<Task> tasks, Ui ui, Storage storage) throws RudraException {
        validateTaskIndex(tasks, this.taskIndex);
        updateTaskStatus(tasks, this.taskIndex, storage, true);
        ui.showTaskMarked(tasks.get(this.taskIndex));
    }
}
