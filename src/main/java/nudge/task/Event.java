package nudge.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that occurs between specified start and end dates.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an incomplete event with the specified description and dates.
     *
     * @param description description of the event.
     * @param startDate date on which the event starts.
     * @param endDate date on which the event ends.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        assert startDate != null : "Event start date should not be null";
        assert endDate != null : "Event end date should not be null";
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the event's start date.
     *
     * @return start date.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the event's end date.
     *
     * @return end date.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Returns the event formatted with its task type, status, and dates.
     *
     * @return formatted event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + startDate.format(DISPLAY_FORMATTER)
                + " to: " + endDate.format(DISPLAY_FORMATTER) + ")";
    }
}
