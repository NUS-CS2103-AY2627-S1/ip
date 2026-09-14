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
    private final LocalDateTime queryTime;
    /** Whether the query explicitly included a time. */
    private final boolean isTimeIncluded;
    /**
     * Creates an upcoming-task query.
     * @param queryTime target date or date-time
     * @param isTimeIncluded whether the query includes a time
     */
    public UpcomingCommand(LocalDateTime queryTime, boolean isTimeIncluded) {
        assert queryTime != null : "Upcoming date must not be null.";
        this.queryTime = queryTime;
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
        StringBuilder outputMessage = new StringBuilder(formatHeader());
        boolean hasMatchingTask = false;
        for (int i = 0; i < taskList.getSize(); i++) {
            Task task = taskList.getTaskByIndex(i);
            if (!isTaskRelevantToQuery(task)) {
                continue;
            }
            outputMessage.append("    ").append(i + 1).append('.').append(task).append('\n');
            hasMatchingTask = true;
        }
        ui.showMessage(outputMessage.toString());
        if (!hasMatchingTask) {
            ui.showMessage("The mirror finds no deadlines or events for that appointed hour.");
        }
    }

    /** Formats the heading for the upcoming-task results. */
    private String formatHeader() {
        String pattern = isTimeIncluded ? "MMM dd yyyy HH:mm" : "MMM dd yyyy";
        DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
        return "Arrodes gazes into the appointed hour and reveals requests for "
                + queryTime.format(displayFormat) + ":\n";
    }

    /** Returns whether a task should be included in the upcoming-task results. */
    private boolean isTaskRelevantToQuery(Task task) {
        LocalDate targetDate = queryTime.toLocalDate();
        LocalDateTime deadlineCutoff = getDeadlineCutoff(targetDate);
        if (task instanceof Deadline deadline) {
            return !deadline.getDueBy().isAfter(deadlineCutoff);
        }

        if (task instanceof Event event) {
            return isTimeIncluded ? isEventAtTime(event, queryTime) : isEventOnDate(event, targetDate);
        }

        return false;
    }

    /** Returns the latest deadline that is included by this query. */
    private LocalDateTime getDeadlineCutoff(LocalDate targetDate) {
        return isTimeIncluded ? queryTime : targetDate.atTime(LocalTime.MAX);
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
