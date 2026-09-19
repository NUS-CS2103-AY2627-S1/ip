package ollie;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that takes place between two dates.
 */
public class Event extends Task {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an event with the given description, start date, and end date.
     *
     * @param description Description of the event.
     * @param startDate Date on which the event starts.
     * @param endDate Date on which the event ends.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the start date of this event.
     *
     * @return Start date of this event.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the end date of this event.
     *
     * @return End date of this event.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Returns whether another task has the same event details.
     *
     * @param other Task to compare with this event.
     * @return True if both events have the same description and date range.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && startDate.equals(((Event) other).startDate)
                && endDate.equals(((Event) other).endDate);
    }

    /**
     * Returns a display-friendly representation of this event.
     *
     * @return Formatted event.
     */
    @Override
    public String toString() {
        return "[event]" + super.toString()
                + " (from: " + startDate.format(DATE_FORMATTER)
                + " to: " + endDate.format(DATE_FORMATTER) + ")";
    }
}
