package trybot.command;

import trybot.exception.TryBotException;
import trybot.storage.Storage;
import trybot.task.TaskList;
import trybot.ui.Ui;

/**
 * Reports that the user entered an unsupported command.
 */
public class UnknownCommand extends Command {
    /**
     * Rejects an unsupported command.
     *
     * @param tasks current task list, unused.
     * @param ui user-interface handler, unused because the caller reports the error.
     * @param storage task persistence handler, unused.
     * @throws TryBotException always, with a prompt for supported commands
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TryBotException {
        throw new TryBotException("I do not recognise that command. Try help to show the list of commands.");
    }

    /**
     * Indicates that an unsupported command does not end the session.
     *
     * @return always false
     */
    @Override
    public boolean isExit() {
        return false;
    }
}
