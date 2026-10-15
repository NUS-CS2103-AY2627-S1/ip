package jiji.command;

import jiji.exception.JijiException;
import jiji.exception.JijiInvalidIndexException;
import jiji.storage.Storage;
import jiji.task.Task;
import jiji.task.TaskList;
import jiji.ui.Ui;

/**
 * Command that marks a task at a specific index as not completed.
 */
public class UnmarkCommand extends Command {

    private final int targetIndex;

    /**
     * Constructs an UnmarkCommand targeting the specified 0-based task index.
     *
     * @param targetIndex The 0-based index of the task to unmark.
     */
    public UnmarkCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    /**
     * Executes the unmark command, updating the task state, saving to storage, and notifying the UI.
     *
     * @param tasks The task list.
     * @param ui The UI handler for displaying output.
     * @param storage The storage handler.
     * @throws JijiException If the target index is out of valid task bounds or saving fails.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws JijiException {
        assert tasks != null : "TaskList dependency cannot be null";
        assert ui != null : "Ui dependency cannot be null";
        assert storage != null : "Storage dependency cannot be null";
        if (targetIndex < 0 || targetIndex >= tasks.size()) {
            throw JijiInvalidIndexException.forInvalidNumber();
        }
        assert targetIndex >= 0 && targetIndex < tasks.size() : "targetIndex must be valid after bounds check";
        Task task = tasks.get(targetIndex);
        assert task != null : "Task to unmark cannot be null";
        task.markAsNotDone();
        storage.save(tasks);
        ui.showTaskUnmarked(task);
        return ui.formatTaskUnmarked(task);
    }
}
