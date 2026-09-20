package atom.command;

import atom.exception.AtomInvalidTypeException;
import atom.exception.AtomMismatchedArgumentsException;
import atom.storage.Storage;
import atom.task.TaskList;

/**
 * A parent class to represent commands.
 * Contains the arguments to the command.
 */
public abstract class Command {
    /** The arguments to the command */
    protected String[] args;

    /**
     * Instantiates a <code>Command</code>.
     *
     * @param args The arguments to the command.
     * @param expected The expected number of arguments.
     * @throws AtomMismatchedArgumentsException If the number of arguments given
     *     does not match the expected number.
     */
    public Command(String[] args, int expected) throws AtomMismatchedArgumentsException {
        checkArgumentsLength(expected, args.length);
        this.args = args;
    }

    /**
     * Executes the command.
     *
     * @param tasks The current list of tasks.
     * @param storage The storage.
     * @return  The output message.
     */
    public abstract String execute(TaskList tasks, Storage storage);

    /**
     * Retrieves the name of the command.
     *
     * @return The command name.
     */
    public abstract String getCommandName();

    /**
     * Gets output message from the command.
     *
     * @param outputArgs The required arguments to the output message.
     * @return The output message.
     */
    protected abstract String getOutputMessage(String ... outputArgs);

    /**
     * Asserts if the arguments length is equal to the expected length.
     * Used when providing the arguments for <code>Command::getOutputMessage</code>.
     *
     * @param commandName The name of the command.
     * @param expected The expected number of arguments.
     * @param found The number of arguments received.
     */
    protected void assertArgumentsLength(String commandName, int expected, int found) {
        assert expected == found : String.format(
                "Output message for %s requires %d arguments but received %d.",
                commandName, expected, found);
    }

    /**
     * Checks if an argument can be parsed as an integer.
     *
     * @param arg The argument as a <code>String</code>.
     * @throws AtomInvalidTypeException If the argument cannot be parsed as an integer
     */
    protected void checkIntegerArgument(String arg)
            throws AtomInvalidTypeException {
        try {
            Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new AtomInvalidTypeException(getCommandName());
        }
    }

    /**
     * Checks if the arguments length is equal to the expected length.
     *
     * @param expected The expected number of arguments.
     * @param found The number of arguments received.
     * @throws AtomMismatchedArgumentsException If the number of arguments given does
     *      not match the expected number.
     */
    private void checkArgumentsLength(int expected, int found)
            throws AtomMismatchedArgumentsException {
        if (expected != found) {
            throw new AtomMismatchedArgumentsException(getCommandName(), expected, found);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Command c = (Command) obj;
        for (int i = 0; i < args.length; i++) {
            if (!args[i].equals(c.args[i])) {
                System.out.print(args[i]);
                System.out.print(c.args[i]);
                return false;
            }
        }
        return true;
    }
}
