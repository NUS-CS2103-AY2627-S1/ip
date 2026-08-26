package ultron.command;

import ultron.exception.UltronException;
import ultron.model.TaskList;
import ultron.storage.Storage;
import ultron.ui.Ui;

/**
 * Represents one executable chatbot command.
 */
public abstract class Command {

    /**
     * Carries out this command using the chatbot's main components.
     *
     * @param tasks the current task list
     * @param ui the user interface
     * @param storage the task storage
     * @throws UltronException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws UltronException;

    /**
     * Returns whether this command ends the chatbot session.
     *
     * @return {@code true} only for the exit command
     */
    public boolean isExit() {
        return false;
    }
}
