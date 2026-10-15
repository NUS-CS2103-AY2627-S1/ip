package nova.command;

import nova.exception.NovaException;
import nova.storage.Storage;
import nova.task.TaskList;
import nova.ui.Ui;

/** An action that can be executed by Nova. */
public abstract class CommandAction {
    /** Executes this action using the application's collaborators. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException;

    /** Returns whether executing this action should end the application. */
    public boolean isExit() {
        return false;
    }
}
