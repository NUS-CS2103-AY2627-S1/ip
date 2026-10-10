package jasper.command;

import jasper.JasperException;
import jasper.task.TaskList;

/**
 * Represents a command to mark a task(s) as completed.
 */
public class MarkCommand implements Command {
    /** Valid command formats message */
    private static final String USAGE_MSG = """
            Usage (with positive integers indices):
                mark N (single task)
                mark start..stop (multiple tasks, both tasks inclusive, start <= stop)
            """;
    /** 0-based first index of tasks in range to be marked as done */
    private final int startIndex;
    /** 0-based last index of tasks in range to be marked as done */
    private final int stopIndex;

    /**
     * Constructs a MarkCommand by parsing the task index(es).
     *
     * @param arg The argument string containing the 1-based task index(es).
     * @throws JasperException If the index is not a valid integer.
     */
    public MarkCommand(String arg) throws JasperException {
        String[] parts = arg.split("\\.\\.");
        if (parts.length > 2) {
            throw new JasperException(USAGE_MSG);
        }
        try {
            startIndex = Integer.parseInt(parts[0]) - 1;
            stopIndex = (parts.length == 1) ? startIndex : Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException e) {
            throw new JasperException(USAGE_MSG);
        }
    }

    @Override
    public CommandResult execute(TaskList tasks) throws JasperException {
        String marked = tasks.mark(startIndex, stopIndex);
        String output = "Alright! I've marked these tasks as done:" + marked.indent(2);
        return new CommandResult(CommandType.MARK, output);
    }
}
