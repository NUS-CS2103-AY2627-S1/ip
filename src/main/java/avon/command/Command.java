package avon.command;

import avon.exception.AvonException;
import avon.exception.InvalidTaskNumberException;
import avon.storage.Storage;
import avon.task.TaskList;
import avon.ui.Ui;

/**
 * Represents an instruction that can act on Avon's application state.
 */
public abstract class Command {
    /**
     * Executes this command.
     *
     * @param taskList the in-memory task list.
     * @param ui the command-line interface.
     * @param storage the persistent task storage.
     * @throws AvonException if the command cannot be completed.
     */
    public abstract void execute(TaskList taskList, Ui ui, Storage storage) throws AvonException;

    /**
     * Returns whether Avon should stop after executing this command.
     *
     * @return {@code true} only for an exit command.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Converts a one-based task number into a validated zero-based index.
     *
     * @param taskList the task list against which to validate the number.
     * @param taskNumber the one-based task number entered by the user.
     * @param commandKeyword the keyword of the command being executed.
     * @return the corresponding zero-based task index.
     * @throws InvalidTaskNumberException if the task number does not identify a stored task.
     */
    protected int getTaskIndex(TaskList taskList, int taskNumber, String commandKeyword)
            throws InvalidTaskNumberException {
        if (taskList.isEmpty()) {
            throw new InvalidTaskNumberException(commandKeyword,
                    "Thy task list is empty; add a task first.", false);
        }
        if (taskNumber <= 0 || taskNumber > taskList.size()) {
            throw new InvalidTaskNumberException(commandKeyword,
                    "Choose a task number from 1 to " + taskList.size() + ".");
        }
        return taskNumber - 1;
    }
}
