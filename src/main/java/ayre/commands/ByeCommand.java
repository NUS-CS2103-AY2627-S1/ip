package ayre.commands;

import java.util.List;

import ayre.CommandResult;
import ayre.LiveTaskList;
import ayre.enums.AyreStatus;

/**
 * Defines how user command "bye" should be validated and executed.
 */
public class ByeCommand extends Command {
    /**
     * Constructs a new ByeCommand.
     *
     * @param tasks Not required for anything.
     */
    public ByeCommand(LiveTaskList tasks) {
    }

    /**
     * Does nothing by default as there is nothing to validate.
     *
     * @param args Expected to be an empty List.
     */
    @Override
    public void validate(List<String> args) {
        assert args.isEmpty() : "Argument list should be empty";
    }

    /**
     * Returns a new CommandResult signaling for process termination.
     *
     * @param args A list of arguments, should be empty at this point but the method doesn't use it regardless.
     * @return A CommandResult holding the (legacy) message String and the status to TERMINATE.
     */
    @Override
    protected CommandResult execute(List<String> args) {
        assert args.isEmpty() : "Argument list should be empty";
        return new CommandResult("~ Terminating connection. See you again, Raven.\n", AyreStatus.TERMINATE);
    }
}
