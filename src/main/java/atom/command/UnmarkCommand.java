package atom.command;

import atom.exception.AtomInvalidTypeException;
import atom.exception.AtomMismatchedArgumentsException;
import atom.exception.AtomTaskNotFoundException;
import atom.storage.Storage;
import atom.task.TaskList;

/**
 * Represents an unmark command.
 */
public class UnmarkCommand extends Command {
    private static final String COMMAND_NAME = "unmark";

    private static final int EXPECTED_ARGUMENTS = 1;
    private static final int EXPECTED_OUTPUT_ARGUMENTS = 1;

    /**
     * Instantiates an <code>UnmarkCommand</code>.
     *
     * @param args The arguments to the command.
     * @throws AtomMismatchedArgumentsException If the number of arguments given
     *     does not match the expected number.
     * @throws AtomInvalidTypeException If the argument given is not an integer.
     */
    public UnmarkCommand(String ... args)
            throws AtomMismatchedArgumentsException, AtomInvalidTypeException {
        super(args, EXPECTED_ARGUMENTS);
        checkIntegerArgument(args[0]);
    }

    @Override
    public String execute(TaskList tasks, Storage storage)
            throws AtomTaskNotFoundException {
        int idx = Integer.parseInt(args[0]);
        tasks.unmark(idx - 1);
        storage.unmarkTask(idx);
        return getOutputMessage(tasks.get(idx - 1).toString());
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    protected String getOutputMessage(String ... outputArgs) {
        assertArgumentsLength(getCommandName(), EXPECTED_OUTPUT_ARGUMENTS, outputArgs.length);
        return "Ok. Marking this task as undone:\n"
                + outputArgs[0];
    }
}
