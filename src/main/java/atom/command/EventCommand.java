package atom.command;

import atom.exception.AtomInvalidTypeException;
import atom.exception.AtomMismatchedArgumentsException;
import atom.storage.Storage;
import atom.task.Event;
import atom.task.TaskList;

/**
 * Represents an event command.
 */
public class EventCommand extends Command {
    private static final String COMMAND_NAME = "event";

    private static final int EXPECTED_ARGUMENTS = 3;
    private static final int EXPECTED_OUTPUT_ARGUMENTS = 2;

    /** The prefix to indicate the start time argument */
    private static final String FROM_PREFIX = "from ";
    /** The prefix to indicate the end time argument */
    private static final String TO_PREFIX = "to ";

    /**
     * Instantiates a <code>EventCommand</code>.
     *
     * @param args The arguments to the command.
     * @throws AtomMismatchedArgumentsException If the number of arguments given
     *     does not match the expected number.
     */
    public EventCommand(String ... args)
            throws AtomMismatchedArgumentsException, AtomInvalidTypeException {
        super(args, EXPECTED_ARGUMENTS);
        removeArgumentPrefix();
    }

    @Override
    public String execute(TaskList tasks, Storage storage) {
        Event event = new Event(args[0].strip(),
                args[1].strip(),
                args[2].strip());
        tasks.add(event);
        storage.writeTask(Storage.TaskType.E, args);
        return getOutputMessage(event.toString(), Integer.toString(tasks.size()));
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
     * Removes the prefixes from the start time and end time arguments.
     *
     * @throws AtomInvalidTypeException If the arguments do not have the correct prefix.
     */
    private void removeArgumentPrefix() throws AtomInvalidTypeException {
        checkArgumentPrefix();
        args[1] = args[1].substring(FROM_PREFIX.length()).strip();
        args[2] = args[2].substring(TO_PREFIX.length()).strip();
    }

    /**
     * Checks if the start time and end time arguments have the correct prefix.
     *
     * @throws AtomInvalidTypeException If the arguments do not have the correct prefix.
     */
    private void checkArgumentPrefix() throws AtomInvalidTypeException {
        if (!args[1].startsWith(FROM_PREFIX) || !args[2].startsWith(TO_PREFIX)) {
            throw new AtomInvalidTypeException(getCommandName());
        }
    }
}
