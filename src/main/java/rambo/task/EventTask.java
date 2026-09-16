package rambo.task;

import java.time.LocalDate;

import rambo.parser.Parser;

/**
 * Represents an event task with a name, from date, and to date.
 */
public class EventTask extends Task {
    private final LocalDate fromDate;
    private final LocalDate toDate;

    /**
     * Constructs a new event task with the given name and date range.
     *
     * @param taskName the name of the task
     * @param fromDateText the start date string in YYYY-MM-DD format
     * @param toDateText the end date string in YYYY-MM-DD format
     */
    public EventTask(String taskName, String fromDateText, String toDateText) {
        super(taskName);

        this.fromDate = Parser.parseDate(fromDateText);
        this.toDate = Parser.parseDate(toDateText);
    }

    /**
     * Converts the task into the required string format for storage.
     *
     * @return the data string representation of the task
     */
    @Override
    public String toDataString() {
        return String.format("E|%s|%s|%s|%s|%d",
                isDone() ? "X" : "", getTaskName(), fromDate, toDate, getPriorityLevel());
    }

    /**
     * Returns a string representation of the task suitable for display.
     *
     * @return the task string representation
     */
    @Override
    public String toString() {
        return String.format("[E][%s]%s %s (from: %s to: %s)",
                isDone() ? "X" : "",
                getPriorityTag(),
                getTaskName(),
                fromDate.format(DISPLAY_FORMAT),
                toDate.format(DISPLAY_FORMAT));
    }
}
