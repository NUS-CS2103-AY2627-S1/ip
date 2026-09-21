package yqr.command;

import yqr.exception.YqrException;
import yqr.storage.Storage;
import yqr.task.TaskList;
import yqr.ui.Ui;

/**
 * Represents an instruction that can be executed by the chatbot.
 */
public abstract class Command {
    /**
     * Creates a command.
     */
    protected Command() {
    }

    /**
     * Executes this command using the application's components.
     *
     * @param tasks task list on which the command operates.
     * @param ui user interface used to display the result.
     * @param storage storage used to persist changes.
     * @throws YqrException if the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws YqrException;

    /**
     * Returns whether this command should end the application.
     *
     * @return {@code true} if the application should exit.
     */
    public boolean isExit() {
        return false;
    }
}
