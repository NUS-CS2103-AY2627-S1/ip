package ayre.commands;

import java.util.List;

import ayre.CommandResult;
import ayre.LiveTaskList;
import ayre.enums.AyreStatus;

/**
 * Defines how a user command "list" should be validated and executed.
 */
public class ListCommand extends Command {
    private final LiveTaskList tasks;

    /**
     * Injects the LiveTaskList to be mutated.
     * @param tasks The list.
     */
    public ListCommand(LiveTaskList tasks) {
        this.tasks = tasks;
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
     * Fetches the printout of the current LiveTaskList.
     *
     * @param args A list of arguments, should be empty at this point but the method doesn't use it regardless.
     * @return A CommandResult holding the operation result message as a String and the update to the process status.
     */
    @Override
    protected CommandResult execute(List<String> args) {
        assert args.isEmpty() : "Argument list should be empty";
        return new CommandResult(tasks.toString(), AyreStatus.CONTINUE);
    }
}

