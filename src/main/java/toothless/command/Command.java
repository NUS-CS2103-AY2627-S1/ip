package toothless.command;

import toothless.exception.ToothlessException;
import toothless.storage.Storage;
import toothless.storage.StorageException;
import toothless.task.TaskList;
import toothless.ui.Ui;

/**
 * Represents an executable command in the Toothless chatbot.
 */
public abstract class Command {
    /**
     * Creates the shared base of an executable Toothless command.
     */
    public Command() {
    }

    /**
     * Performs this command using the application's collaborators.
     *
     * @param taskList tasks available to the command.
     * @param ui user interface used to present command results.
     * @param storage storage used to persist task changes.
     * @throws ToothlessException if the command cannot be completed
     */
    public abstract void execute(TaskList taskList, Ui ui, Storage storage)
            throws ToothlessException;

    /**
     * Saves a proposed list before publishing it, leaving the original unchanged on failure.
     *
     * @param taskList active tasks to replace only after a successful save.
     * @param proposedTasks validated tasks to persist.
     * @param ui user interface used to report a save failure.
     * @param storage storage used to persist the tasks.
     * @return true if both saving and publication succeeded.
     */
    protected final boolean saveTasks(TaskList taskList, TaskList proposedTasks, Ui ui, Storage storage) {
        try {
            storage.save(proposedTasks);
            taskList.replaceWith(proposedTasks);
            return true;
        } catch (StorageException exception) {
            ui.showSaveError();
            return false;
        }
    }

    /**
     * Returns whether this command should end the application loop.
     *
     * @return true when the application should stop after execution
     */
    public boolean isExit() {
        return false;
    }
}
