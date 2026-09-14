package johnny.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that spans a specific time period.
 * Dates are stored as LocalDate and displayed as MMM dd yyyy.
 */
public class Event extends Task {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an event task spanning a date range.
     *
     * @param description the task description
     * @param startDate the start date
     * @param endDate the end date
     * @throws IllegalArgumentException if the start date is not before the end date
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        if (!startDate.isBefore(endDate)) {
            throw new IllegalArgumentException("Event start date must be before end date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** Returns file format: {@code E | done | description | from | to}. */
    @Override
    public String toFileString() {
        return "E | " + super.toFileString() + " | " + startDate + " | " + endDate;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + startDate.format(DISPLAY_FORMAT)
                + " to: " + endDate.format(DISPLAY_FORMAT) + ")";
    }
}
