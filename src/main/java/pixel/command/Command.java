package pixel.command;

import java.io.IOException;

import pixel.storage.Storage;
import pixel.task.TaskList;
import pixel.ui.Ui;

/**
 * Represents an executable instruction understood by Pixel.
 */
public abstract class Command {

    /** Creates an executable command. */
    protected Command() {
    }

    /**
     * Performs this command's action using the application's collaborators.
     *
     * @param tasks Application task list.
     * @param ui Console user interface.
     * @param storage Task persistence service.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage);

    /**
     * Indicates whether Pixel should stop after executing this command.
     *
     * @return {@code true} only for an exit command.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Indicates whether this command represents invalid user input.
     *
     * @return {@code true} only for commands that should use error presentation.
     */
    public boolean isError() {
        return false;
    }

    /**
     * Saves task changes while keeping Pixel usable if the write fails.
     *
     * @param tasks Task list to save.
     * @param ui User interface used to report a write failure.
     * @param storage Task persistence service.
     */
    protected void saveTasksSafely(TaskList tasks, Ui ui, Storage storage) {
        try {
            storage.save(tasks);
        } catch (IOException | SecurityException exception) {
            ui.showMessage("Oops! I couldn't save your tasks. "
                    + "Your changes will only last until Pixel exits.");
        }
    }
}
