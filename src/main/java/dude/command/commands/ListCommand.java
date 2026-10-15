package dude.command.commands;

import dude.command.core.Command;
import dude.command.core.CommandResult;
import dude.command.support.CommandEnvironment;

/**
 * Displays all tasks in their current order.
 */
public final class ListCommand extends Command {
    /**
     * Creates a list command.
     *
     * @param environment Command collaborators and receiver.
     */
    public ListCommand(CommandEnvironment environment) {
        super(environment);
    }

    @Override
    public CommandResult execute() {
        environment().ui().showTaskList(tasks());
        return CommandResult.continueRunning();
    }
}
