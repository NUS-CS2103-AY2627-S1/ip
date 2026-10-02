package floppy;

import java.time.LocalDate;

/**
 * Represents a task that occurs over a period of time.
 */
public class Event extends Task {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an event with the specified description and date range.
     *
     * @param description Description of the event.
     * @param startDate Start date of the event.
     * @param endDate End date of the event.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description, TaskType.EVENT);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the event's start date.
     *
     * @return Start date of this event.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the event's end date.
     *
     * @return End date of this event.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + startDate.format(DISPLAY_DATE_FORMAT)
                + " to: " + endDate.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
