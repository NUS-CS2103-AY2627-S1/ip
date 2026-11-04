package soar.command;

import java.io.IOException;

import soar.exception.InvalidTaskNumberException;
import soar.exception.SoarException;
import soar.exception.StorageException;
import soar.parser.CommandType;
import soar.storage.Storage;
import soar.task.TaskList;
import soar.ui.Ui;

/**
 * Represents one user command that can act on the application's components.
 */
public abstract class Command {
    /** Message shown when a task-list change cannot be safely persisted. */
    private static final String SAVE_ERROR_MESSAGE =
            "I couldn't save the task data, so that change was not kept. "
                    + "Please check the data file and try again.";

    /**
     * Creates a command that can operate on the application's components.
     */
    protected Command() {
    }

    /**
     * Performs this command's behavior.
     *
     * @param tasks Task collection on which the command can operate.
     * @param ui User interface used to present the result.
     * @param storage Persistence service for commands that change tasks.
     * @throws SoarException if the command cannot be completed safely
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws SoarException;

    /**
     * Converts a user-facing task number to an index after checking the current list.
     *
     * @param taskNumber One-based number entered by the user.
     * @param tasks Current task list.
     * @param commandType Numbered command requesting the task.
     * @return validated zero-based task index
     * @throws InvalidTaskNumberException if the selected task does not exist
     */
    protected final int requireTaskIndex(int taskNumber, TaskList tasks,
            CommandType commandType) throws InvalidTaskNumberException {
        int taskCount = tasks.size();
        String commandWord = commandType.getCommandWord();
        if (taskNumber < 1 || taskNumber > taskCount) {
            if (taskCount == 0) {
                throw new InvalidTaskNumberException(
                        "Your task list is an open sky right now. Add a task before using '"
                                + commandWord + "'!");
            }
            throw new InvalidTaskNumberException("Task " + taskNumber
                    + " is outside your list. Choose a number from 1 to " + taskCount
                    + " and we'll stay on course!");
        }
        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < taskCount
                : "A validated task number should map to an existing task index";
        return taskIndex;
    }

    /**
     * Persists a changed task list and reverses the change if saving fails.
     *
     * @param storage Persistence service.
     * @param tasks Changed task list.
     * @param rollback Action that restores the previous in-memory state.
     * @throws StorageException if the changed list cannot be saved
     */
    protected final void saveChange(Storage storage, TaskList tasks, Runnable rollback)
            throws StorageException {
        try {
            storage.save(tasks.asList());
        } catch (IOException | RuntimeException e) {
            rollback.run();
            throw new StorageException(SAVE_ERROR_MESSAGE);
        }
    }

    /**
     * Reports whether executing this command should end the application loop.
     *
     * @return {@code false} for commands that keep Soar running
     */
    public boolean isExit() {
        return false;
    }
}
