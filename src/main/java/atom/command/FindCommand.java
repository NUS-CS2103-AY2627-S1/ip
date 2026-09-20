package atom.command;

import atom.exception.AtomMismatchedArgumentsException;
import atom.storage.Storage;
import atom.task.TaskList;

/**
 * Represents a find command.
 */
public class FindCommand extends Command {
    private static final String COMMAND_NAME = "find";

    private static final int EXPECTED_ARGUMENTS = 1;
    private static final int EXPECTED_OUTPUT_ARGUMENTS = 1;

    /**
     * Instantiates a <code>FindCommand</code>.
     *
     * @param args The arguments to the command.
     * @throws AtomMismatchedArgumentsException If the number of arguments given
     *     does not match the expected number.
     */
    public FindCommand(String ... args) throws AtomMismatchedArgumentsException {
        super(args, EXPECTED_ARGUMENTS);
    }

    @Override
    public String execute(TaskList tasks, Storage storage) {
        return getOutputMessage(tasks.find(args[0]).toString());
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    protected String getOutputMessage(String ... outputArgs) {
        assertArgumentsLength(getCommandName(), EXPECTED_OUTPUT_ARGUMENTS, outputArgs.length);
        if (outputArgs[0].isEmpty()) {
            return "There are no matching tasks in your list.";
        }
        return "I've found these matching tasks in your list:\n"
                + outputArgs[0];
    }
}
