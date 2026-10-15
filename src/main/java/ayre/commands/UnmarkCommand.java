package ayre.commands;

import java.util.List;

import ayre.CommandResult;
import ayre.LiveTaskList;
import ayre.ValidationTools;
import ayre.enums.AyreStatus;
import ayre.exceptions.InvalidCommandArgumentsException;
import ayre.exceptions.InvalidCommandException;

/**
 * Defines how a user command "unmark ..." should be validated and executed.
 */
public class UnmarkCommand extends Command {
    private static final int EXPECTED_ARGUMENT_COUNT = 1;

    private final LiveTaskList tasks;

    /**
     * Injects the LiveTaskList to be mutated.
     * @param tasks The list.
     */
    public UnmarkCommand(LiveTaskList tasks) {
        this.tasks = tasks;
    }

    /**
     * Checks whether the argument is a numeric String.
     *
     * @param args Expected to be a List with a single element that is the argument for the Command.
     * @throws InvalidCommandException If argument is not a numeric String.
     */
    @Override
    public void validate(List<String> args) throws InvalidCommandException {
        assert args.size() == EXPECTED_ARGUMENT_COUNT : "Argument list should have "
                + EXPECTED_ARGUMENT_COUNT + " elements";
        if (ValidationTools.isInvalidTaskIndex(args.getFirst())) {
            throw new InvalidCommandArgumentsException("~ Raven, please provide me with an integer value.");
        }
    }

    /**
     * Unmarks the Task at the matching LiveTaskList index (starts from 0) to the user-provided index (starts from 1)
     *
     * @param args A list containing the index of the Task to be unmarked complete
     * @return Record holding the operation result message as a String and the update to the process status
     * @throws InvalidCommandException If index is out of bounds
     */
    @Override
    protected CommandResult execute(List<String> args) throws InvalidCommandException {
        assert args.size() == EXPECTED_ARGUMENT_COUNT : "Argument list should have "
                + EXPECTED_ARGUMENT_COUNT + " elements";
        int index = super.validateIndex(args, this.tasks);
        String resultMsg = this.tasks.unmark(index);
        return new CommandResult(resultMsg, AyreStatus.CONTINUE);
    }
}
