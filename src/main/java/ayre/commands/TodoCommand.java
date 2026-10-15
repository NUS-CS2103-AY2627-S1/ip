package ayre.commands;

import java.util.List;

import ayre.CommandResult;
import ayre.LiveTaskList;
import ayre.enums.AyreStatus;
import ayre.tasks.Todo;

/**
 * Defines how a user command "todo ..." should be validated and executed.
 */
public class TodoCommand extends Command {
    private static final int EXPECTED_ARGUMENT_COUNT = 1;

    private final LiveTaskList tasks;

    /**
     * Injects the LiveTaskList to be mutated.
     * @param tasks The list.
     */
    public TodoCommand(LiveTaskList tasks) {
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
     * Adds a new Todo to the LiveTaskList.
     *
     * @param args A list of arguments to construct a new Todo with.
     * @return Record holding the operation result message as a String and the update to the process status.
     */
    @Override
    protected CommandResult execute(List<String> args) {
        assert args.size() == EXPECTED_ARGUMENT_COUNT : "Argument list should have "
                + EXPECTED_ARGUMENT_COUNT + " elements";
        String resultMsg = tasks.add(new Todo(args.getFirst()));
        return new CommandResult(resultMsg, AyreStatus.CONTINUE);
    }
}
