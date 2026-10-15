package greenchonk.task;

import java.time.LocalDate;

/**
 * Represents a task that takes place between a start and end date.
 */
public class Event extends Task {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an incomplete event with the given description and time range.
     *
     * @param description the event description.
     * @param startDate the event's starting date.
     * @param endDate the event's ending date.
     * @throws IllegalArgumentException if the ending date is before the starting date.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("An event cannot end before it starts.");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns this event's starting date.
     *
     * @return the event's starting date.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns this event's ending date.
     *
     * @return the event's ending date.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && startDate.equals(((Event) other).startDate)
                && endDate.equals(((Event) other).endDate);
    }

    @Override
    public Event withDescription(String description) {
        Event editedEvent = new Event(description, startDate, endDate);
        copyStatusTo(editedEvent);
        return editedEvent;
    }

    /**
     * Returns a copy of this event with a replacement starting date.
     * The copy retains this event's description, ending date, and completion status.
     *
     * @param startDate the replacement starting date.
     * @return the updated event copy.
     * @throws IllegalArgumentException if the replacement date is after the ending date.
     */
    public Event withStartDate(LocalDate startDate) {
        Event editedEvent = new Event(getDescription(), startDate, endDate);
        copyStatusTo(editedEvent);
        return editedEvent;
    }

    /**
     * Returns a copy of this event with a replacement ending date.
     * The copy retains this event's description, starting date, and completion status.
     *
     * @param endDate the replacement ending date.
     * @return the updated event copy.
     * @throws IllegalArgumentException if the replacement date is before the starting date.
     */
    public Event withEndDate(LocalDate endDate) {
        Event editedEvent = new Event(getDescription(), startDate, endDate);
        copyStatusTo(editedEvent);
        return editedEvent;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    /**
     * Returns the task display text with its formatted date range.
     *
     * @return the event's display text
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + TaskDate.format(startDate)
                + " to: " + TaskDate.format(endDate) + ")";
    }
}
