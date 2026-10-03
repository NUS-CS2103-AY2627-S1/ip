package ramly.command;

import ramly.model.TaskList;
import ramly.storage.Storage;
import ramly.ui.Ui;

/** Represents an executable user command. */
public abstract class Command {
    /** Executes this command using the application's collaborators. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage);

    /** Returns whether executing this command should end the application. */
    public boolean isExit() {
        return false;
    }
}
