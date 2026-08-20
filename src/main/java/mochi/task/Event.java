package mochi.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that starts and ends at specific dates.
 */
public class Event extends Task {
    protected LocalDate from;
    protected LocalDate to;

    /**
     * Creates an event task with the given description and date range.
     *
     * @param description the task description
     * @param from        the start date
     * @param to          the end date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(TaskType.EVENT, description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[" + type.getSymbol() + "]" + super.toString()
                + " (from: " + from.format(DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH))
                + " to: " + to.format(DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH)) + ")";
    }

    @Override
    public String toFileString() {
        return super.toFileString() + " | " + from.format(DateTimeFormatter.ISO_LOCAL_DATE)
                + " | " + to.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
