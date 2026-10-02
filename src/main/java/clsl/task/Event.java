package clsl.task;

import java.time.LocalDate;

/**
 * Represents a task occurring from a start date through an end date.
 */
public class Event extends Task {
    private final LocalDate startDate;
    private final String formattedStartDate;
    private final LocalDate endDate;
    private final String formattedEndDate;

    /**
     * Creates an incomplete event task.
     *
     * @param name Description of the task.
     * @param startDate Start date in {@code yyyy-mm-dd} format.
     * @param endDate End date in {@code yyyy-mm-dd} format.
     */
    public Event(String name, String startDate, String endDate) {
        super(name);
        this.startDate = LocalDate.parse(startDate);
        this.formattedStartDate = this.startDate.format(DISPLAY_DATE_FORMAT);
        this.endDate = LocalDate.parse(endDate);
        this.formattedEndDate = this.endDate.format(DISPLAY_DATE_FORMAT);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + formattedStartDate
                + " to: " + formattedEndDate + ")";
    }

    /**
     * Returns this event in the storage format.
     *
     * @return The task prefixed with its event marker and date range.
     */
    @Override
    public String getSaveString() {
        return "E | " + super.getSaveString() + " | " + startDate + " | " + endDate;
    }

    @Override
    public boolean isDuplicateOf(Task other) {
        return other instanceof Event
                && super.isDuplicateOf(other)
                && startDate.equals(((Event) other).startDate)
                && endDate.equals(((Event) other).endDate);
    }

    /**
     * Returns whether this event occurs on the specified date, inclusively.
     *
     * @param date Date to check.
     * @return {@code true} if the date is within the event's range, or {@code false} otherwise.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}
