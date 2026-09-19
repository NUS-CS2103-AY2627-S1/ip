package nyonbot.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import nyonbot.Logic.Result;
import nyonbot.Parser;
import nyonbot.model.Deadline;
import nyonbot.model.Event;
import nyonbot.model.NyonException;
import nyonbot.model.Task;
import nyonbot.model.TaskList;

/**
 * Lists dated tasks that occur on a requested day.
 */
public class OnCommand extends Command {
    private static final DateTimeFormatter DAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private TaskList list;

    /**
     * Creates an on command with parsed arguments and a task list.
     *
     * @param arguments parsed command arguments
     * @param list task list to search
     */
    public OnCommand(HashMap<String, String> arguments, TaskList list) {
        super(arguments);
        this.list = list;
    }

    /** {@inheritDoc} */
    @Override
    public Result execute() throws NyonException {
        String dayValue = arguments.get(DESCRIPTION_KEY);
        if (dayValue == null || dayValue.isBlank()) {
            throw new NyonException("specify a day in the format dd/MM/yyyy");
        }

        LocalDate requestedDay = Parser.parseDay(dayValue);
        if (requestedDay == null) {
            throw new NyonException("enter the day in the format dd/MM/yyyy");
        }

        TaskList matchingTasks = new TaskList();
        for (Task task : list) {
            if (occursOn(task, requestedDay)) {
                matchingTasks.add(task);
            }
        }

        String formattedDay = requestedDay.format(DAY_FORMATTER);
        if (matchingTasks.isEmpty()) {
            return new Result("No deadlines or events on " + formattedDay + ".");
        }

        String taskOutput = new ListCommand(arguments, matchingTasks).execute().out();
        return new Result(String.format("Tasks on %s:%s", formattedDay, taskOutput));
    }

    private boolean occursOn(Task task, LocalDate requestedDay) {
        if (task instanceof Deadline deadline) {
            return deadline.getDeadline().toLocalDate().equals(requestedDay);
        }
        if (task instanceof Event event) {
            LocalDate startDay = event.getEventTimes()[0].toLocalDate();
            LocalDate endDay = event.getEventTimes()[1].toLocalDate();
            return !requestedDay.isBefore(startDay) && !requestedDay.isAfter(endDay);
        }
        return false;
    }
}
