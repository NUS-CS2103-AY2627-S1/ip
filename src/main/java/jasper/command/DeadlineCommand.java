package jasper.command;

import jasper.JasperException;
import jasper.parser.Parser;
import jasper.task.Deadline;
import jasper.task.Task;
import jasper.task.TaskList;

/**
 * Represents a command to add a deadline task.
 */
public class DeadlineCommand implements Command {
    /** Keyword used to separate task from deadline */
    private static final String BY_KW = "/by";
    /** Valid command format message */
    private static final String USAGE_MSG = String.format("Usage: deadline <task> %s <datetime>", BY_KW);
    /** Deadline task to be added */
    private final Task task;

    /**
     * Constructs a DeadlineCommand by parsing the provided arguments.
     *
     * @param arg The argument string containing the task description and deadline datetime.
     * @throws JasperException If the argument format is invalid.
     */
    public DeadlineCommand(String arg) throws JasperException {
        int sep = arg.lastIndexOf(BY_KW);
        if (sep == -1) {
            throw new JasperException(USAGE_MSG);
        }
        String description = arg.substring(0, sep).strip();
        String datetime = arg.substring(sep + BY_KW.length()).strip();
        if (description.isEmpty() || datetime.isEmpty()) {
            throw new JasperException(USAGE_MSG);
        }
        task = new Deadline(description, Parser.parseDateTime(datetime));
    }

    @Override
    public CommandResult execute(TaskList tasks) throws JasperException {
        tasks.add(task);
        return new CommandResult(CommandType.DEADLINE, "Aye, aye. I've added this task:\n  " + task);
    }
}
