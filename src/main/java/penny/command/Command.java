package penny.command;

import penny.common.PennyException;
import penny.storage.Storage;
import penny.task.TaskList;
import penny.ui.Ui;

/**
 * Represents an executable command in the Penny application.
 * Defines the contract for executing user commands with access to TaskList, Ui, and Storage.
 */
public abstract class Command {

    /**
     * Constructs a base Command instance.
     */
    protected Command() {
    }

    /**
     * Executes the command.
     *
     * @param tasks The task list manipulated by the command.
     * @param ui The user interface for displaying feedback.
     * @param storage The storage handler for saving tasks.
     * @return The response string produced by the command execution.
     * @throws PennyException If execution fails due to invalid parameters or state.
     */
    public abstract String execute(TaskList tasks, Ui ui, Storage storage) throws PennyException;

    /**
     * Indicates whether this command signals the chatbot to exit.
     *
     * @return True if this is an exit command, false otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
