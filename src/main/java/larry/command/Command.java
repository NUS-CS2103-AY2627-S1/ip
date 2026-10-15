package larry.command;

import java.io.IOException;

import larry.exception.LarryException;
import larry.storage.Storage;
import larry.task.TaskList;
import larry.ui.Ui;

/**
 * Represents a user command that can be executed by Larry.
 */
public abstract class Command {
    /**
     * Executes the command using Larry's application components.
     *
     * @param tasks Task list affected by the command.
     * @param ui UI used to display the command result.
     * @param storage Storage used to persist task changes.
     * @throws LarryException If the command cannot be executed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws LarryException;

    /**
     * Checks whether executing this command should terminate Larry.
     *
     * @return True when Larry should terminate after executing the command.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Validates that an index identifies a task currently in the task list.
     *
     * @param tasks Task list containing the requested task.
     * @param taskIndex Zero-based task index to validate.
     * @throws LarryException If the index does not identify a stored task.
     */
    protected void validateTaskIndex(TaskList tasks, int taskIndex) throws LarryException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new LarryException("EVIL LARRY cannot find that task number in his vault.");
        }
    }

    /**
     * Saves the task list and displays a warning if persistence fails.
     *
     * @param tasks Tasks to save.
     * @param ui UI used to display a saving warning.
     * @param storage Storage receiving the tasks.
     */
    protected void saveTasks(TaskList tasks, Ui ui, Storage storage) {
        try {
            storage.saveTasks(tasks);
        } catch (IOException | SecurityException e) {
            ui.showSavingError(e.getMessage());
        }
    }
}
