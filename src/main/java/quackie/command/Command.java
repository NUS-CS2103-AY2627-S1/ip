package quackie.command;

import java.io.IOException;

import quackie.storage.Storage;
import quackie.task.TaskList;
import quackie.ui.Ui;

/**
 * Represents one parsed Quackie command.
 */
public abstract class Command {
    /** Creates a command instance. */
    protected Command() {
    }

    /**
     * Executes this command using the supplied application services.
     *
     * @param tasks the current task list
     * @param ui the console user interface
     * @param storage the task storage service
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage);

    /**
     * Indicates whether this command ends the current session.
     *
     * @return {@code true} for an exit command, or {@code false} otherwise
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the task list and reports a write failure without stopping the session.
     *
     * @param tasks the task list to save
     * @param ui the console user interface used to report failures
     * @param storage the task storage service
     */
    protected void saveTasks(TaskList tasks, Ui ui, Storage storage) {
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            ui.showError("I couldn't save your tasks.");
        }
    }
}
