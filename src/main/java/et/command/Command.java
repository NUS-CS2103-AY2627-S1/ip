package et.command;

import et.exception.ETException;
import et.storage.Storage;
import et.task.TaskList;
import et.ui.Ui;

import java.io.IOException;

/**
 * Represents an action that ET can perform in response to one user command.
 */
public abstract class Command {
    /**
     * Performs this command using ET's application components.
     *
     * @param tasks ET's current task list
     * @param ui the component used to communicate with the user
     * @param storage the component used to persist tasks
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ETException;

    /**
     * Saves the current task list and reports an error if saving fails.
     *
     * @param tasks ET's current task list
     * @param ui the component used to communicate with the user
     * @param storage the component used to persist tasks
     */
    protected void saveTasks(TaskList tasks, Ui ui, Storage storage) {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            ui.showSavingError();
        }
    }

    /**
     * Returns whether executing this command should stop ET.
     *
     * @return {@code true} when ET should exit after this command
     */
    public boolean isExit() {
        return false;
    }
}
