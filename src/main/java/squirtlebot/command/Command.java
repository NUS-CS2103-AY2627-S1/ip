package squirtlebot.command;

import squirtlebot.exception.CommandException;
import squirtlebot.storage.Storage;
import squirtlebot.task.TaskList;
import squirtlebot.ui.Ui;

/**
 * Provides a base implementation for commands used in {@code SquirtleBot}.
 * Subclasses must implement the logic for running commands by overriding the
 * {@link #execute(TaskList, Ui, Storage)} method.
 */
public abstract class Command {

    /**
     * Performs the actions of the command, applying its actions to the given task list,
     * user interface, and storage.
     *
     * @param tasks list containing tasks created previously by the user.
     * @param ui interface used to display output to the user.
     * @param storage storage handler used to persist changes made by the command.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage);


    /**
     * Indicates whether the user-issued command was to exit the program.
     *
     * @return true if the issued command is to exit {@code SquirtleBot}; false otherwise.
     */
    public boolean shouldExit() {
        return false;
    }

    /**
     * Updates storage file by writing {@code tasks} to it.
     * Common implementation used across different commands.
     *
     * @param tasks list of tasks to be written to storage.
     * @param storage interface for storage-related operations.
     */
    protected void updateStorage(TaskList tasks, Storage storage) {
        storage.writeData(tasks);
    }

    /**
     * Verifies if the user supplied index is valid given the state of the provided task list.
     *
     * @param index index of the task list.
     * @param tasks list of tasks.
     * @throws CommandException if index is out of bounds for the provided task list.
     */
    protected void validateIndex(int index, TaskList tasks) {
        if (index < 0 || index >= tasks.size()) {
            throw new CommandException("Please enter a valid index :(");
        }
    }
}
