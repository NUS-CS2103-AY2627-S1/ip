package koara.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that takes place between a start and end date.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an incomplete event.
     *
     * @param description Description of the event.
     * @param startDate Start date of the event.
     * @param endDate End date of the event.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(TaskType.EVENT, description, "");
        assert startDate != null : "Event start date must not be null";
        assert endDate != null : "Event end date must not be null";
        assert !startDate.isAfter(endDate) : "Event must not end before it starts";
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns this event with its start and end dates in display format.
     *
     * @return Display representation of this event.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + startDate.format(DISPLAY_DATE_FORMAT)
                + " to: " + endDate.format(DISPLAY_DATE_FORMAT) + ")";
    }

    /**
     * Returns this event in the line-based format used by the data file.
     *
     * @return Serialized event data.
     */
    @Override
    public String toDataString() {
        return super.toDataString() + " | " + startDate + " | " + endDate;
    }
}
