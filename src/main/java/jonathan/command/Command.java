package jonathan.command;

import java.io.IOException;

import jonathan.JonathanException;
import jonathan.storage.Storage;
import jonathan.task.TaskList;
import jonathan.ui.UI;
/** Represents a user command that can be executed by the chatbot. */
public abstract class Command {
    /** Executes the command using the provided dependencies. */
    public abstract void execute(TaskList tasks, UI ui, Storage storage) throws JonathanException, IOException;

    /**
     * Returns whether executing this command should end the chatbot loop.
     *
     * @return {@code true} only for an exit command
     */
    public boolean isExit() {
        return false;
    }
}
