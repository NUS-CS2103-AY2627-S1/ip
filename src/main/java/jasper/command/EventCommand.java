package jasper.command;

import jasper.JasperException;
import jasper.parser.Parser;
import jasper.task.Event;
import jasper.task.Task;
import jasper.task.TaskList;

/**
 * Represents a command to add an event task.
 */
public class EventCommand implements Command {
    /** Keyword separating task and start datetime */
    private static final String FROM_KW = "/from";
    /** Keyword separating start and end datetime */
    private static final String TO_KW = "/to";
    /** Valid command format message */
    private static final String USAGE_MSG = String.format(
            "Usage: event <task> %s <datetime> %s <datetime>", FROM_KW, TO_KW
    );
    /** Event task to be added */
    private final Task task;

    /**
     * Constructs an EventCommand by parsing the task description and timeframe.
     *
     * @param arg The argument string containing the task details, start datetime, and end datetime.
     * @throws JasperException If the argument format is invalid.
     */
    public EventCommand(String arg) throws JasperException {
        int sepFrom = arg.lastIndexOf(FROM_KW);
        int sepTo = arg.lastIndexOf(TO_KW);
        if (sepFrom == -1 || sepTo == -1 || sepTo < sepFrom) {
            throw new JasperException(USAGE_MSG);
        }
        String description = arg.substring(0, sepFrom).strip();
        String datetimeFrom = arg.substring(sepFrom + FROM_KW.length(), sepTo).strip();
        String datetimeTo = arg.substring(sepTo + TO_KW.length()).strip();
        if (description.isEmpty() || datetimeFrom.isEmpty() || datetimeTo.isEmpty()) {
            throw new JasperException(USAGE_MSG);
        }
        task = new Event(description, Parser.parseDateTime(datetimeFrom), Parser.parseDateTime(datetimeTo));
    }

    @Override
    public CommandResult execute(TaskList tasks) throws JasperException {
        tasks.add(task);
        return new CommandResult(CommandType.EVENT, "Aye, aye. I've added this task:\n  " + task);
    }
}
