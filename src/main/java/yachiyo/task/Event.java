package yachiyo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents an event that takes place between a start and end date-time.
 */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an event with the given description, start date-time, and end date-time.
     *
     * @param description description of the event.
     * @param from date and time when the event starts.
     * @param to date and time when the event ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        assert from != null : "Event start date-time must not be null";
        assert to != null : "Event end date-time must not be null";
        assert to.isAfter(from) : "Event must end after it starts";

        this.from = from;
        this.to = to;
    }

    /**
     * Returns the date and time when this event starts.
     *
     * @return event start date and time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the date and time when this event ends.
     *
     * @return event end date and time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Returns a copy of this event with the specified description.
     *
     * @param description replacement description.
     * @return event containing the replacement description.
     */
    @Override
    public Event withDescription(String description) {
        return copyCompletionStatusTo(new Event(description, from, to));
    }

    /**
     * Returns a copy of this event with the specified start date-time.
     *
     * @param from replacement start date and time.
     * @return event containing the replacement start date-time.
     */
    public Event withFrom(LocalDateTime from) {
        return copyCompletionStatusTo(new Event(getDescription(), from, to));
    }

    /**
     * Returns a copy of this event with the specified end date-time.
     *
     * @param to replacement end date and time.
     * @return event containing the replacement end date-time.
     */
    public Event withTo(LocalDateTime to) {
        return copyCompletionStatusTo(new Event(getDescription(), from, to));
    }

    /**
     * Returns this event in the format used by the storage file.
     *
     * @return Stored event representation.
     */
    @Override
    public String toFileFormat() {
        return String.format("EVENT | %s | %s | %s", super.toFileFormat(),
                TaskDateTimeFormatter.formatForStorage(this.from),
                TaskDateTimeFormatter.formatForStorage(this.to));
    }

    /**
     * Returns whether this event takes place on the specified date.
     *
     * @param date Date to check.
     * @return True if the event includes the date; false otherwise.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = this.from.toLocalDate();
        LocalDate endDate = this.to.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Returns this event in its user-facing display format.
     *
     * @return Formatted event description, status, and duration.
     */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s, to: %s)", super.toString(),
                TaskDateTimeFormatter.formatForDisplay(this.from),
                TaskDateTimeFormatter.formatForDisplay(this.to));
    }
}
