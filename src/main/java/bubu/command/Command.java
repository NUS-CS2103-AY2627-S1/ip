package bubu.command;

import bubu.exception.BubuException;
import bubu.storage.Storage;
import bubu.task.TaskList;
import bubu.ui.Ui;

/**
 * Represents one executable chatbot command.
 */
public abstract class Command {
    /**
     * Performs this command using the application's collaborators.
     *
     * @param tasks current task list.
     * @param ui user interface for output.
     * @param storage persistent task storage.
     * @throws BubuException if the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws BubuException;

    /**
     * Indicates whether running this command should end the chatbot.
     *
     * @return true only for the exit command.
     */
    public boolean isExit() {
        return false;
    }
}
