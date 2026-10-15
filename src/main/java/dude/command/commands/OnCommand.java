package dude.command.commands;

import java.time.LocalDate;

import dude.command.core.Command;
import dude.command.core.CommandResult;
import dude.command.support.CommandEnvironment;
import dude.exception.UsageException;
import dude.parser.Parser;

/**
 * Displays tasks occurring on a requested date.
 */
public final class OnCommand extends Command {
    private final String argument;

    /**
     * Creates a date-query command.
     *
     * @param argument    Date argument.
     * @param environment Command collaborators and receiver.
     */
    public OnCommand(String argument, CommandEnvironment environment) {
        super(environment);
        this.argument = argument;
    }

    @Override
    public CommandResult execute() throws UsageException {
        LocalDate date = Parser.parseDate(argument);
        environment().ui().showTasksOnDate(tasks(), date);
        return CommandResult.continueRunning();
    }
}
