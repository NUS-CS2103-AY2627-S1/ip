package atom.command;

import atom.exception.AtomMismatchedArgumentsException;
import atom.storage.Storage;
import atom.task.TaskList;

/**
 * Represents a list command.
 */
public class ListCommand extends Command {
    private static final String COMMAND_NAME = "list";

    private static final int EXPECTED_ARGUMENTS = 0;
    private static final int EXPECTED_OUTPUT_ARGUMENTS = 1;

    /**
     * Instantiates a <code>ListCommand</code>.
     *
     * @param args The arguments to the command.
     * @throws AtomMismatchedArgumentsException If the number of arguments given
     *     does not match the expected number.
     */
    public ListCommand(String ... args) throws AtomMismatchedArgumentsException {
        super(args, EXPECTED_ARGUMENTS);
    }

    @Override
    public String execute(TaskList tasks, Storage storage) {
        if (tasks.size() == 0) {
            return "There are no tasks in your list.";
        }
        return getOutputMessage(tasks.toString());
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    protected String getOutputMessage(String ... outputArgs) {
        assertArgumentsLength(getCommandName(), EXPECTED_OUTPUT_ARGUMENTS, outputArgs.length);
        return "Here are the tasks in your list:\n"
                + outputArgs[0];
    }
}
