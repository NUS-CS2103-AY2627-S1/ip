package thaisbot.command;

import thaisbot.Storage;
import thaisbot.ThaisBotException;
import thaisbot.Ui;
import thaisbot.task.TaskList;

/**
 * Abstract command representing an action triggered by user input.
 * Concrete subclasses implement execute(...) to perform the action.
 */
public abstract class Command {
    /**
     * Execute the command.
     * @param tasks task list to operate on
     * @param ui ui helper for interaction
     * @param storage storage for persisting changes
     * @throws ThaisBotException for expected error conditions
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ThaisBotException;

    /**
     * Returns true if this command causes the application to exit.
     * @return true if this is an exit command
     */
    public boolean isExit() {
        return false;
    }
}
