package dude.command.commands;

import dude.command.core.Command;
import dude.command.core.CommandResult;
import dude.command.core.CommandType;
import dude.command.support.CommandEnvironment;
import dude.exception.UsageDetails;
import dude.exception.UsageException;

/**
 * Displays tasks whose descriptions contain a keyword.
 */
public final class FindCommand extends Command {
    private final String argument;

    /**
     * Creates a find command.
     *
     * @param argument    Search keyword.
     * @param environment Command collaborators and receiver.
     */
    public FindCommand(String argument, CommandEnvironment environment) {
        super(environment);
        this.argument = argument;
    }

    @Override
    public CommandResult execute() throws UsageException {
        if (argument == null || argument.isBlank()) {
            throw new UsageException(new UsageDetails("find", "keyword", "<missing>",
                    "a non-blank keyword", CommandType.FIND.getUsageMessage(), "<keyword>"));
        }
        environment().ui().showMatchingTasks(receiver().findMatches(argument.trim()));
        return CommandResult.continueRunning();
    }
}
