package nova.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Represents a task that takes place between a start and an end date or time.
 */
public class Event extends Task {
    private final LocalDateTime startDateTime;
    private final boolean startHasTime;
    private final LocalDateTime endDateTime;
    private final boolean endHasTime;

    /**
     * Creates an incomplete event with the given description and time range.
     *
     * @param description description of the event.
     * @param startDateTime date and optional time at which the event starts.
     * @param startHasTime whether the start includes an explicit time.
     * @param endDateTime date and optional time at which the event ends.
     * @param endHasTime whether the end includes an explicit time.
     */
    public Event(String description, LocalDateTime startDateTime, boolean startHasTime,
            LocalDateTime endDateTime, boolean endHasTime) {
        super(description);
        assert startDateTime != null : "Event start date/time must not be null";
        assert endDateTime != null : "Event end date/time must not be null";
        assert !endDateTime.isBefore(startDateTime) : "Event must not end before it starts";
        assert startHasTime || startDateTime.toLocalTime().equals(LocalTime.MIDNIGHT)
                : "A date-only event start must use midnight";
        assert endHasTime || endDateTime.toLocalTime().equals(LocalTime.MIDNIGHT)
                : "A date-only event end must use midnight";
        this.startDateTime = startDateTime;
        this.startHasTime = startHasTime;
        this.endDateTime = endDateTime;
        this.endHasTime = endHasTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTypeIcon() {
        return "E";
    }

    /**
     * Returns this event's start date and optional time.
     *
     * @return start date/time.
     */
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    /**
     * Returns whether the event start includes an explicit time.
     *
     * @return {@code true} if the start includes a time.
     */
    public boolean hasStartTime() {
        return startHasTime;
    }

    /**
     * Returns this event's end date and optional time.
     *
     * @return end date/time.
     */
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    /**
     * Returns whether the event end includes an explicit time.
     *
     * @return {@code true} if the end includes a time.
     */
    public boolean hasEndTime() {
        return endHasTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Formats this event for storage, including its start and end values.
     *
     * @return task fields followed by the stored event range.
     */
    @Override
    public String toFileString() {
        return super.toFileString() + " | "
                + TaskDateTime.formatForStorage(startDateTime, startHasTime)
                + " | " + TaskDateTime.formatForStorage(endDateTime, endHasTime);
    }

    /**
     * Formats this event for display, including its start and end values.
     *
     * @return display form of this event.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: "
                + TaskDateTime.formatForDisplay(startDateTime, startHasTime)
                + " to: " + TaskDateTime.formatForDisplay(endDateTime, endHasTime) + ")";
    }
}
