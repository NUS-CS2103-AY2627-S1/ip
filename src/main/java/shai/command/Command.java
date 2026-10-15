package shai.command;

import shai.exception.ShaiException;
import shai.storage.Storage;
import shai.task.TaskList;
import shai.ui.Ui;

/**
 * Represents one command that can be executed by Shai.
 */
public abstract class Command {
    /**
     * Executes this command using the current application state and services.
     *
     * @param tasks the current task list
     * @param ui the user interface
     * @param storage the task storage
     * @throws ShaiException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ShaiException;

    /**
     * Returns whether this command requests that Shai stop running.
     *
     * @return {@code true} when the application should exit
     */
    public boolean isExit() {
        return false;
    }

    /** Returns whether this command needs to write the task file. */
    public boolean requiresStorageWrite() {
        return false;
    }
}
