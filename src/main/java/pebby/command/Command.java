package pebby.command;

import java.io.IOException;

import pebby.storage.Storage;
import pebby.task.TaskList;
import pebby.ui.Ui;

/**
 * Represents one action that Pebby can perform in response to a user command.
 *
 * <p>Subclasses contain the behavior for one command, keeping command-specific
 * work out of Pebby's main loop.</p>
 */
public abstract class Command {
    /**
     * Performs this command using the current task list, UI, and storage.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage);

    /**
     * Returns whether this command should end Pebby's command loop.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the current tasks and converts a save failure into a UI warning.
     */
    protected String saveTasks(TaskList tasks, Storage storage) {
        try {
            storage.save(tasks.asList());
            return "";
        } catch (IOException | IllegalArgumentException exception) {
            return "\nWarning: your task was changed, but Pebby could not save it: "
                    + exception.getMessage();
        }
    }
}
