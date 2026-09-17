package potatobot.logic.command;

import potatobot.logic.CommandResult;
import potatobot.model.task.TaskList;
import potatobot.storage.Storage;

/**
 * Signals that PotatoBot should finish the current session.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that exits PotatoBot.
     */
    public ExitCommand() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(TaskList tasks, Storage storage) {
        return CommandResult.exit("Bye. I'm rolling back to the potato patch.");
    }
}
