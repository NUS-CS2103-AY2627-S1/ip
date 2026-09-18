package meka.command;

import java.io.IOException;

import meka.exception.MekaException;
import meka.storage.Storage;
import meka.task.TaskList;
import meka.ui.Ui;

/**
 * Represents an executable instruction entered by the user.
 */
public abstract class Command {
    /**
     * Creates a command for execution by MEKA.
     */
    protected Command() {
    }

    /**
     * Executes this command using the application's task list, UI, and storage.
     *
     * @param tasks task list to read or modify.
     * @param ui user interface through which results are displayed.
     * @param storage storage component used to persist changes.
     * @throws MekaException if the command cannot be applied to the task list.
     * @throws IOException if a task-list change cannot be saved.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws MekaException, IOException;

    /**
     * Returns whether this command should end the application.
     *
     * @return false for commands that keep MEKA running.
     */
    public boolean isExit() {
        return false;
    }
}
