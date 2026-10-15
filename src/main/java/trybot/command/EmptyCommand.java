package trybot.command;

import trybot.exception.TryBotException;
import trybot.storage.Storage;
import trybot.task.TaskList;
import trybot.ui.Ui;

/**
 * Reports that the user entered a blank command.
 */
public class EmptyCommand extends Command {
    /**
     * Rejects the blank input represented by this command.
     *
     * @param tasks current task list, unused.
     * @param ui user-interface handler, unused because the caller reports the error.
     * @param storage task persistence handler, unused.
     * @throws TryBotException always, with a prompt for a valid command
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TryBotException {
        throw new TryBotException("I need a command. Try todo, list, or bye.");
    }

    /**
     * Indicates that an empty command does not end the session.
     *
     * @return always false
     */
    @Override
    public boolean isExit() {
        return false;
    }
}
