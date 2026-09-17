package potatobot.logic.command;

import potatobot.logic.CommandResult;
import potatobot.model.task.TaskList;
import potatobot.storage.Storage;

/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that displays every task.
     */
    public ListCommand() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandResult execute(TaskList tasks, Storage storage) {
        return CommandResult.reply(tasks.printList());
    }
}
