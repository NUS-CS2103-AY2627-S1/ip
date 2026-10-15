package yourhand.commands;

import yourhand.exceptions.StorageException;
import yourhand.exceptions.YourHandException;
import yourhand.storage.Storage;
import yourhand.tasks.TaskList;
import yourhand.ui.Ui;

import java.io.IOException;

/**
 * Represents one user command that can change or display the application state.
 */
public abstract class Command {
    /** Executes this command using the application's collaborators. */
    public abstract void execute(TaskList taskList, Ui ui, Storage storage) throws YourHandException;

    /** Returns whether this command should end the application. */
    public boolean isExit() {
        return false;
    }

    /** Saves tasks and translates file-writing failures into application errors. */
    protected final void saveTasks(TaskList taskList, Storage storage) throws YourHandException {
        try {
            storage.save(taskList);
        } catch (IOException | SecurityException exception) {
            throw new StorageException();
        }
    }
}
