package bob.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that occurs between two dates.
 */
public class Event extends Task {
    private static final DateTimeFormatter DATE_FORMATTER_FOR_DISPLAY =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an event task.
     *
     * @param description Description of the event.
     * @param startDate Start date of the event.
     * @param endDate End date of the event.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the event start date.
     *
     * @return Start date of this event.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the event end date.
     *
     * @return End date of this event.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Returns the event task representation used for data storage.
     *
     * @return Data representation containing the task type, status, description, and dates.
     */
    @Override
    public String toDataString() {
        return "E | " + (isDone() ? "1" : "0")
                + " | " + this.description
                + " | " + this.startDate
                + " | " + this.endDate;
    }

    /**
     * Returns a display representation containing the task details and formatted dates.
     *
     * @return Display representation of this event task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + startDate.format(DATE_FORMATTER_FOR_DISPLAY)
                + " to: " + endDate.format(DATE_FORMATTER_FOR_DISPLAY) + ")";
    }
}
