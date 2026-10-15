package dude.command.commands;

import dude.command.core.Command;
import dude.command.core.CommandResult;
import dude.command.support.CommandEnvironment;

/**
 * Terminates the application after displaying the farewell message.
 */
public final class ExitCommand extends Command {
    /**
     * Creates an exit command.
     *
     * @param environment Command collaborators and receiver.
     */
    public ExitCommand(CommandEnvironment environment) {
        super(environment);
    }

    @Override
    public CommandResult execute() {
        environment().ui().showGoodbye();
        return CommandResult.exiting();
    }
}
