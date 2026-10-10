package jasper.command;

import jasper.JasperException;
import jasper.task.TaskList;

/**
 * Represents a command to exit the application.
 */
public class ByeCommand implements Command {
    /** Valid command format message */
    private static final String USAGE_MSG = "Usage: bye";

    /**
     * Constructs a ByeCommand and validates the argument.
     *
     * @param arg The argument string which must be empty.
     * @throws JasperException If the argument string is not empty.
     */
    public ByeCommand(String arg) throws JasperException {
        if (!arg.isEmpty()) {
            throw new JasperException(USAGE_MSG);
        }
    }

    @Override
    public CommandResult execute(TaskList tasks) {
        return new CommandResult(CommandType.QUIT, "Farewell. Hope to see you again soon!");
    }
}
