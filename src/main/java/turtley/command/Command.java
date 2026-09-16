package turtley.command;

import turtley.exception.TurtleyException;
import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;

/**
 * Represents one executable Turtley command.
 */
public abstract class Command {

    /**
     * Creates a command.
     */
    protected Command() {
    }

    /**
     * Executes this command using the application's collaborators.
     *
     * @param tasks the application's task list.
     * @param ui the application's user interface.
     * @param storage the application's persistence service.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage);

    /**
     * Returns whether this command should terminate the application.
     *
     * @return {@code true} only for the exit command.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the task list and restores a previous mutation if saving fails.
     *
     * @param tasks the task list to save.
     * @param storage the persistence service.
     * @param rollback the operation that restores the previous state.
     */
    protected void saveOrRollback(TaskList tasks, Storage storage, Runnable rollback) {
        try {
            storage.save(tasks.asList());
        } catch (TurtleyException exception) {
            rollback.run();
            throw exception;
        }
    }
}
