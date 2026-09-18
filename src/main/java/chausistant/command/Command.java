package chausistant.command;

import java.io.IOException;

import chausistant.exception.ChausistantException;
import chausistant.storage.Storage;
import chausistant.task.TaskList;
import chausistant.ui.Ui;

/**
 * Represents one parsed instruction that the chatbot can execute.
 *
 * <p>Concrete command classes will gradually take responsibility for their
 * own behavior as the task list and storage code are extracted.</p>
 */
public abstract class Command {

    /** Performs this command's action using the application's current collaborators. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws ChausistantException, IOException;

    /** Returns whether executing this command should end the chatbot session. */
    public boolean isExit() {
        return false;
    }
}
