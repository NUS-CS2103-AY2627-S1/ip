package greenchonk.command;

import greenchonk.exception.GreenChonkException;
import greenchonk.storage.Storage;
import greenchonk.task.TaskList;
import greenchonk.ui.Ui;

/**
 * Represents an action requested by the user.
 */
public abstract class Command {
    /**
     * Creates a command that can be executed by Green Chonk.
     */
    public Command() {
    }

    /**
     * Executes this command using the application's components.
     *
     * @param tasks the tasks currently stored.
     * @param ui the user interface through which results are shown.
     * @param storage the storage used to persist task changes.
     * @throws GreenChonkException if the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws GreenChonkException;

    /**
     * Returns whether this command ends the application.
     *
     * @return true if this command exits the application.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Converts a user-facing task number into a valid zero-based index.
     *
     * @param taskNumber the one-based task number entered by the user.
     * @param commandName the command word being executed.
     * @param tasks the tasks currently stored.
     * @return the corresponding zero-based task index.
     * @throws GreenChonkException if the task number does not identify a task.
     */
    protected static int getTaskIndex(int taskNumber, String commandName,
            TaskList tasks) throws GreenChonkException {
        if (tasks.isEmpty()) {
            throw new GreenChonkException("There are no tasks to " + commandName + " yet.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new GreenChonkException("Task " + taskNumber
                    + " does not exist. Choose a number from 1 to " + tasks.size() + ".");
        }

        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Validated task number must map to an existing task index";
        return taskIndex;
    }
}
