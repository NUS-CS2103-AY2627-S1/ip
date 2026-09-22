package mono.task;

import java.time.LocalDate;

import mono.parser.DateParser;

/**
 * A task that takes place between two calendar dates.
 */
public class Event extends Task {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an event from ISO-8601 date strings.
     *
     * @param name event description
     * @param startDateText start date in {@code yyyy-MM-dd} format
     * @param endDateText end date in {@code yyyy-MM-dd} format
     * @throws java.time.format.DateTimeParseException if either date is invalid
     */
    public Event(String name, String startDateText, String endDateText) {
        super(name);
        this.startDate = DateParser.parseDate(startDateText);
        this.endDate = DateParser.parseDate(endDateText);
    }

    /**
     * Returns the event's stable, pipe-delimited save-file representation.
     *
     * @return task type, completion status, name, start time, and end time
     */
    @Override
    public String toFileString() {
        return String.format(
                "E | %d | %s | %s | %s",
                super.isDone ? 1 : 0,
                super.name,
                this.startDate,
                this.endDate);
    }

    /**
     * Returns this event's user-facing representation.
     *
     * @return task type, completion status, name, and formatted date range
     */
    @Override
    public String toString() {
        return String.format(
                "[E][%s] %s (from: %s to: %s)",
                super.isDone ? "X" : " ",
                super.name,
                DateParser.formatDate(this.startDate),
                DateParser.formatDate(this.endDate));
    }
}
