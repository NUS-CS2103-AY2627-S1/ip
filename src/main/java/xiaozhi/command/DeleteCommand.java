package xiaozhi.command;

import xiaozhi.exception.XiaoZhiException;
import xiaozhi.storage.Storage;
import xiaozhi.task.Task;
import xiaozhi.task.TaskList;
import xiaozhi.ui.Ui;

/**
 * Removes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int targetIndex;
    private Task removedTask;

    /**
     * Creates a DeleteCommand for the task at the given position.
     *
     * @param targetIndex 0-based index, within the task list, of the task to delete.
     */
    public DeleteCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    /**
     * Removes the target task, reports it through {@code ui}, and saves
     * the updated list through {@code storage}.
     *
     * @throws XiaoZhiException If {@code targetIndex} is not a valid position in {@code tasks}.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage, CommandHistory history) throws XiaoZhiException {
        if (targetIndex < 0 || targetIndex >= tasks.size()) {
            throw new XiaoZhiException(
                    "There is no task " + (targetIndex + 1) + " to speak of. Only " + tasks.size()
                    + " task(s) exist; look again before you act.");
        }
        removedTask = tasks.remove(targetIndex);
        ui.showRemoved(removedTask, tasks.size());
        storage.save(tasks.asList());
    }

    /**
     * Returns {@code true} -- deleting a task can always be undone by putting it back.
     */
    @Override
    public boolean isUndoable() {
        return true;
    }

    /**
     * Puts the deleted task back at the position it was removed from, reports
     * it through {@code ui}, and saves the updated list through {@code storage}.
     */
    @Override
    public void undo(TaskList tasks, Ui ui, Storage storage) {
        assert removedTask != null : "undo() should only be called after execute() has run.";
        tasks.insert(targetIndex, removedTask);
        ui.showAdded(removedTask, tasks.size());
        storage.save(tasks.asList());
    }
}
