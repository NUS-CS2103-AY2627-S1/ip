package atom.command;

import atom.exception.AtomInvalidTypeException;
import atom.exception.AtomMismatchedArgumentsException;
import atom.storage.Storage;
import atom.task.Deadline;
import atom.task.TaskList;

/**
 * Represents a deadline command.
 */
public class DeadlineCommand extends Command {
    private static final String COMMAND_NAME = "deadline";

    private static final int EXPECTED_ARGUMENTS = 2;
    private static final int EXPECTED_OUTPUT_ARGUMENTS = 2;

    /** The prefix to indicate the deadline argument */
    private static final String BY_PREFIX = "by ";

    /**
     * Instantiates a <code>DeadlineCommand</code>.
     *
     * @param args The arguments to the command.
     * @throws AtomMismatchedArgumentsException If the number of arguments given
     *     does not match the expected number.
     */
    public DeadlineCommand(String ... args)
            throws AtomMismatchedArgumentsException, AtomInvalidTypeException {
        super(args, EXPECTED_ARGUMENTS);
        removeArgumentPrefix();
    }

    @Override
    public String execute(TaskList tasks, Storage storage) {
        Deadline deadline = new Deadline(args[0].strip(),
                args[1].strip());
        tasks.add(deadline);
        storage.writeTask(Storage.TaskType.D, args);
        return getOutputMessage(deadline.toString(), Integer.toString(tasks.size()));
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    protected String getOutputMessage(String ... outputArgs) {
        assertArgumentsLength(getCommandName(), EXPECTED_OUTPUT_ARGUMENTS, outputArgs.length);
        return "Alright! I've added this task:\n"
                + outputArgs[0]
                + "\nYou now have "
                + outputArgs[1]
                + " tasks in the list.";
    }

    /**
     * Removes the prefix from the deadline argument.
     *
     * @throws AtomInvalidTypeException If the argument does not have the correct prefix.
     */
    private void removeArgumentPrefix() throws AtomInvalidTypeException {
        checkArgumentPrefix();
        args[1] = args[1].substring(BY_PREFIX.length()).strip();
    }

    /**
     * Checks if the deadline argument has the correct prefix.
     *
     * @throws AtomInvalidTypeException If the argument does not have the correct prefix.
     */
    private void checkArgumentPrefix() throws AtomInvalidTypeException {
        if (!args[1].startsWith(BY_PREFIX)) {
            throw new AtomInvalidTypeException(getCommandName());
        }
    }
}
