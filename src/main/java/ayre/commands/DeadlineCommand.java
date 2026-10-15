package ayre.commands;

import java.util.List;

import ayre.CommandResult;
import ayre.LiveTaskList;
import ayre.ValidationTools;
import ayre.enums.AyreStatus;
import ayre.exceptions.InvalidCommandArgumentsException;
import ayre.tasks.Deadline;

/**
 * Defines how a user command "deadline ..." should be validated and executed.
 */
public class DeadlineCommand extends Command {
    private static final int EXPECTED_ARGUMENT_COUNT = 2;
    private static final int NAME_INDEX = 0;
    private static final int DATE_INDEX = 1;

    private final LiveTaskList tasks;

    /**
     * Injects the LiveTaskList to be mutated.
     * @param tasks The list.
     */
    public DeadlineCommand(LiveTaskList tasks) {
        this.tasks = tasks;
    }

    /**
     * Checks whether the date argument is a String that matches the ISO_LOCAL_DATE format.
     * The matching is strict, i.e. impossible dates like 13/13 are invalid.
     * Name does not need to be validated.
     *
     * @param args Expected to be a List with a name and the date in that order.
     * @throws InvalidCommandArgumentsException If argument is not a valid ISO_LOCAL_DATE.
     */
    @Override
    public void validate(List<String> args) throws InvalidCommandArgumentsException {
        assert args.size() == EXPECTED_ARGUMENT_COUNT : "Argument list should have "
                + EXPECTED_ARGUMENT_COUNT + " elements";
        if (ValidationTools.isInvalidIsoDate(args.get(DATE_INDEX))) {
            throw new InvalidCommandArgumentsException("~ Raven, I need a date in the "
                    + "ISO_LOCAL_DATE yyyy-mm-dd format");
        }
    }

    /**
     * Adds a new Deadline to the LiveTaskList.
     *
     * @param args A list of arguments to construct a new Deadline with.
     * @return Record holding the operation result message as a String and the update to the process status.
     */
    @Override
    protected CommandResult execute(List<String> args) {
        assert args.size() == EXPECTED_ARGUMENT_COUNT : "Argument list should have "
                + EXPECTED_ARGUMENT_COUNT + " elements";
        String resultMsg = tasks.add(new Deadline(args.get(NAME_INDEX), args.get(DATE_INDEX)));
        return new CommandResult(resultMsg, AyreStatus.CONTINUE);
    }
}
