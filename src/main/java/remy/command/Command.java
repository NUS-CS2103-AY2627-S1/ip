package remy.command;

import remy.storage.Storage;
import remy.storage.StorageException;
import remy.task.TaskList;
import remy.ui.Ui;

/**
 * Represents an action requested by the user.
 *
 * @author LEE-wz
 */
public abstract class Command {

    /**
     * Performs this command using the application's collaborators.
     *
     * @param tasks task collection to operate on
     * @param ui user interface used to display results
     * @param storage persistence service used to save changes
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage);

    /**
     * Returns whether this command ends the application loop.
     *
     * @return true when the application should exit
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves changed tasks and reports any storage failure through the user interface.
     *
     * @param tasks changed task collection
     * @param ui user interface used to display storage errors
     * @param storage persistence service used to save tasks
     */
    protected void saveTasks(TaskList tasks, Ui ui, Storage storage) {
        try {
            storage.save(tasks);
        } catch (StorageException e) {
            ui.showSavingError(e.getMessage());
        }
    }
}
