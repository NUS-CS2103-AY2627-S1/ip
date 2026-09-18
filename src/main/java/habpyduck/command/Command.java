package habpyduck.command;

import habpyduck.HabpyDuckException;
import habpyduck.storage.Storage;
import habpyduck.task.TaskList;
import habpyduck.ui.Ui;

/**
 * Represents one executable user command.
 */
public abstract class Command {
    /**
     * Runs this command.
     *
     * @param tasks the task list to read or update.
     * @param ui the UI used to show command results.
     * @param storage the storage used to save and load tasks.
     * @throws HabpyDuckException if the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws HabpyDuckException;

    /**
     * Returns whether this command should end the program.
     *
     * @return true if this is an exit command.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Checks that a user-facing task number points to an existing task.
     *
     * @param taskNumber the one-based task number entered by the user.
     * @param tasks the task list to check.
     * @return the zero-based index of the task.
     * @throws HabpyDuckException if the task does not exist.
     */
    protected int requireValidTaskIndex(int taskNumber, TaskList tasks) throws HabpyDuckException {
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new HabpyDuckException("OH NO!!! Task " + taskNumber + " does not exist in your list.");
        }
        return taskIndex;
    }
}
