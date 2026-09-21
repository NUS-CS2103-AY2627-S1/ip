package yqr.command;

import yqr.exception.YqrException;
import yqr.storage.Storage;
import yqr.task.TaskList;
import yqr.ui.Ui;

/**
 * Restores the task list to its state before the most recent change.
 */
public class UndoCommand extends Command {
    /**
     * Creates a command that undoes the most recent task change.
     */
    public UndoCommand() {
    }

    /**
     * Restores, displays, and saves the previous task list state.
     *
     * @param tasks task list whose most recent change is undone.
     * @param ui user interface used to display confirmation.
     * @param storage storage used to save the restored list.
     * @throws YqrException if there is no change to undo or the restored list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws YqrException {
        tasks.undo();
        try {
            storage.saveTasks(tasks);
        } catch (YqrException e) {
            tasks.rollbackLastChange();
            throw e;
        }
        tasks.confirmLastChange();
        ui.showUndoSuccess();
    }
}
