package goop.command;

import java.io.IOException;

import goop.exception.GoopException;
import goop.storage.Storage;
import goop.task.TaskList;
import goop.ui.Ui;

/**
 * Represents a validated user command.
 */
public abstract class Command {
    /**
     * Creates a command.
     */
    protected Command() {
    }

    /**
     * Performs this command using the application's collaborators.
     *
     * @param tasks Task list to inspect or update.
     * @param ui User interface used to display the result.
     * @param storage Storage used to persist changes.
     * @throws GoopException If the command cannot be applied to the task list.
     * @throws IOException If a changed task list cannot be saved.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws GoopException, IOException;

    /**
     * Checks whether this command ends the application.
     *
     * @return True only for an exit command.
     */
    public boolean isExit() {
        return false;
    }
}
