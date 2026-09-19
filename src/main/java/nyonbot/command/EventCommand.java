package nyonbot.command;

import java.time.LocalDateTime;
import java.util.HashMap;

import nyonbot.Logic.Result;
import nyonbot.Parser;
import nyonbot.model.Event;
import nyonbot.model.NyonException;
import nyonbot.model.Task;
import nyonbot.model.TaskList;

/**
 * Adds an event with a start time and end time.
 */
public class EventCommand extends Command {
    private TaskList list;

    /**
     * Creates an event command with parsed arguments and a task list.
     *
     * @param arguments parsed command arguments.
     * @param list      task list to add the event to.
     */
    public EventCommand(HashMap<String, String> arguments, TaskList list) {
        super(arguments);
        this.list = list;
    }

    /** {@inheritDoc} */
    @Override
    public Result execute() throws NyonException {
        String description = arguments.get(DESCRIPTION_KEY);
        String startValue = arguments.get("--from");
        String endValue = arguments.get("--to");
        if (description == null || description.isBlank()) {
            throw new NyonException("cannot omit the description");
        }
        if (!arguments.containsKey("--from")) {
            throw new NyonException("use --from to specify the start time");
        }
        if (!arguments.containsKey("--to")) {
            throw new NyonException("use --to to specify the end time");
        }
        if (startValue.isBlank()) {
            throw new NyonException("cannot omit the start time");
        }
        if (endValue.isBlank()) {
            throw new NyonException("cannot omit the end time");
        }

        LocalDateTime startDate = Parser.parseDate(startValue);
        LocalDateTime endDate = Parser.parseDate(endValue);
        if (startDate == null || endDate == null) {
            throw new NyonException(
                    "couldn't read your start date / end date\n"
                    + "please enter start and end times in the format dd/MM/yyyy HHmm");
        }
        if (!startDate.isBefore(endDate)) {
            throw new NyonException("start time must be before end time");
        }

        Task event = new Event(description, startDate, endDate);
        list.add(event);
        return new Result(String.format(
                "I've added this task: %n%s%nThere are %s tasks in your list",
                event, list.size()), false, true);
    }
}
