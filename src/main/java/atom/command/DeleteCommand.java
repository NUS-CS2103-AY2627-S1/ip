package atom.command;

import atom.exception.AtomInvalidTypeException;
import atom.exception.AtomMismatchedArgumentsException;
import atom.exception.AtomTaskNotFoundException;
import atom.storage.Storage;
import atom.task.Task;
import atom.task.TaskList;

/**
 * Represents a delete command.
 */
public class DeleteCommand extends Command {
    private static final String COMMAND_NAME = "delete";

    private static final int EXPECTED_ARGUMENTS = 1;
    private static final int EXPECTED_OUTPUT_ARGUMENTS = 2;

    /**
     * Instantiates a <code>DeleteCommand</code>.
     *
     * @param args The arguments to the command.
     * @throws AtomMismatchedArgumentsException If the number of arguments given
     *     does not match the expected number.
     * @throws AtomInvalidTypeException If the argument given is not an integer.
     */
    public DeleteCommand(String ... args)
            throws AtomMismatchedArgumentsException, AtomInvalidTypeException {
        super(args, EXPECTED_ARGUMENTS);
        checkIntegerArgument(args[0]);
    }

    @Override
    public String execute(TaskList tasks, Storage storage)
            throws AtomTaskNotFoundException {
        int idx = Integer.parseInt(args[0]);
        Task t = tasks.remove(idx - 1);
        storage.deleteTask(idx);
        return getOutputMessage(t.toString(), Integer.toString(tasks.size()));
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    protected String getOutputMessage(String ... outputArgs) {
        assertArgumentsLength(getCommandName(), EXPECTED_OUTPUT_ARGUMENTS, outputArgs.length);
        return "Alright! I've removed this task:\n"
                + outputArgs[0]
                + "\nYou now have "
                + outputArgs[1]
                + " tasks in the list.";
    }
}
