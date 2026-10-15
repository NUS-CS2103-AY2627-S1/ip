package judey.command;

import judey.exception.JudeyException;
import judey.storage.Storage;
import judey.tasklist.TaskList;
import judey.ui.Ui;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    /**
     * Executes the command using the provided task list, UI, and storage.
     *
     * @param tasks task list container
     * @param ui user interface handler
     * @param storage storage disk reader/writer
     * @throws JudeyException if execution fails
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws JudeyException;

    /**
     * Indicates whether executing this command signals the chatbot to terminate.
     *
     * @return true if command exits application; false otherwise
     */
    public boolean isExit() {
        return false;
    }
}
