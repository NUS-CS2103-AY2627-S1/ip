package arrodes.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import arrodes.storage.Storage;
import arrodes.task.Deadline;
import arrodes.task.Event;
import arrodes.task.Task;
import arrodes.task.TaskList;
import arrodes.ui.Ui;

/** Command that displays deadlines and events relevant to a date or time. */
public class UpcomingCommand extends Command {
    /** Date or date-time against which tasks are matched. */
    private final LocalDateTime on;
    /** Whether the query explicitly included a time. */
    private final boolean isTimeIncluded;
    /**
     * Creates an upcoming-task query.
     * @param on target date or date-time
     * @param isTimeIncluded whether the query includes a time
     */
    public UpcomingCommand(LocalDateTime on, boolean isTimeIncluded) {
        assert on != null : "Upcoming date must not be null.";
        this.on = on;
        this.isTimeIncluded = isTimeIncluded;
    }

    /**
     * Displays matching deadlines and events.
     * @param ui interface used for output
     * @param taskList list to search
     * @param storage unused storage
     */
    @Override
    public void execute(Ui ui, TaskList taskList, Storage storage) {
        boolean isDateOnlyQuery = !isTimeIncluded;
        LocalDate targetDate = on.toLocalDate();
        LocalDateTime dayEnd = targetDate.atTime(LocalTime.MAX);
        DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern(
                isDateOnlyQuery ? "MMM dd yyyy" : "MMM dd yyyy HH:mm", Locale.ENGLISH);
        LocalDateTime deadlineCutoff = isDateOnlyQuery ? dayEnd : on;
        boolean hasMatchingTask = false;

        String outputMessage = "Arrodes gazes into the appointed hour and reveals requests for "
                + on.format(displayFormat) + ":\n";
        for (int i = 0; i < taskList.getSize(); i++) {
            Task task = taskList.getTaskByIndex(i);
            if (isTaskRelevantToQuery(task, targetDate, on, deadlineCutoff, isDateOnlyQuery)) {
                outputMessage = outputMessage + "    " + (i + 1) + "." + task + "\n";
                hasMatchingTask = true;
            }
        }
        ui.showMessage(outputMessage);
        if (!hasMatchingTask) {
            ui.showMessage("The mirror finds no deadlines or events for that appointed hour.");
        }
    }

    /** Returns whether a task should be included in the upcoming-task results. */
    private boolean isTaskRelevantToQuery(Task task, LocalDate targetDate, LocalDateTime queryTime,
                                          LocalDateTime deadlineCutoff, boolean isDateOnlyQuery) {
        if (task instanceof Deadline deadline) {
            return !deadline.getDueBy().isAfter(deadlineCutoff);
        }

        if (task instanceof Event event) {
            return isDateOnlyQuery
                    ? isEventOnDate(event, targetDate)
                    : isEventAtTime(event, queryTime);
        }

        return false;
    }

    /** Returns whether an event overlaps the queried date. */
    private boolean isEventOnDate(Event event, LocalDate targetDate) {
        return !event.getStartAt().toLocalDate().isAfter(targetDate)
                && !event.getEndAt().toLocalDate().isBefore(targetDate);
    }

    /** Returns whether an event contains the queried date and time. */
    private boolean isEventAtTime(Event event, LocalDateTime queryTime) {
        return !event.getStartAt().isAfter(queryTime) && !event.getEndAt().isBefore(queryTime);
    }
}
