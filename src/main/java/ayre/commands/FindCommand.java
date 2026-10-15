package ayre.commands;

import java.util.List;

import ayre.CommandResult;
import ayre.LiveTaskList;
import ayre.enums.AyreStatus;

/**
 * Defines how a user command "find ..." should be validated and executed.
 */
public class FindCommand extends Command {
    private static final int EXPECTED_ARGUMENT_COUNT = 1;

    private final LiveTaskList tasks;

    /**
     * Injects the LiveTaskList to be mutated.
     * @param tasks The list.
     */
    public FindCommand(LiveTaskList tasks) {
        this.tasks = tasks;
    }

    /**
     * Does nothing by default as the name does not need to be validated.
     *
     * @param args Expected to be a List with a single element that is the name.
     */
    @Override
    public void validate(List<String> args) {
        assert args.size() == EXPECTED_ARGUMENT_COUNT : "Argument list should have "
                + EXPECTED_ARGUMENT_COUNT + " elements";
    }

    /**
     * Finds all tasks that contains the user-inputted name as a substring.
     *
     * @param args A list containing the substring to match for.
     * @return Record holding the operation result message as a String and the update to the process status.
     */
    @Override
    protected CommandResult execute(List<String> args) {
        assert args.size() == EXPECTED_ARGUMENT_COUNT : "Argument list should have "
                + EXPECTED_ARGUMENT_COUNT + " elements";
        return new CommandResult(tasks.findTasks(args.getFirst()), AyreStatus.CONTINUE);
    }
}

