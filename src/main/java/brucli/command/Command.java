package brucli.command;

import java.io.IOException;

import brucli.storage.Storage;
import brucli.task.TaskList;
import brucli.ui.ResponseType;
import brucli.ui.Ui;

/**
 * Represents an executable user command.
 */
public abstract class Command {

    /**
     * Executes this command using the application's collaborators.
     */
    public abstract void execute(
            TaskList tasks,
            Ui ui,
            Storage storage
    ) throws IOException;

    /**
     * Returns whether executing this command should end the application.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Returns the visual category used to display this command's response.
     */
    public ResponseType getResponseType() {
        return ResponseType.STANDARD;
    }
}
