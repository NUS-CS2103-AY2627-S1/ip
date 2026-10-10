package jasper.command;

import jasper.JasperException;
import jasper.task.TaskList;

/**
 * Represents a command to list all tasks.
 */
public class ListCommand implements Command {
    /** Valid command format message */
    private static final String USAGE_MSG = "Usage: list";

    /**
     * Constructs a ListCommand and validates the argument.
     *
     * @param arg The argument string which must be empty.
     * @throws JasperException If the argument string is not empty.
     */
    public ListCommand(String arg) throws JasperException {
        if (!arg.isEmpty()) {
            throw new JasperException(USAGE_MSG);
        }
    }

    @Override
    public CommandResult execute(TaskList tasks) {
        if (tasks.isEmpty()) {
            return new CommandResult(CommandType.LIST, "No tasks here! Add some to track.");
        }
        return new CommandResult(CommandType.LIST, "Here are your tasks:\n" + tasks);
    }
}
