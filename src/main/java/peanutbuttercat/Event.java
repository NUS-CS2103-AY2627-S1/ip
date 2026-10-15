package peanutbuttercat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a task with a stated start and end date and time. */
public class Event extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy h:mm a");
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an incomplete event with the given description and time range.
     *
     * @param description Description of the event.
     * @param from Date and time at which the event starts.
     * @param to Date and time at which the event ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        if (from == null || to == null) {
            throw new IllegalArgumentException("Event dates cannot be blank.");
        }
        if (!to.isAfter(from)) {
            throw new IllegalArgumentException("Event end time must be after its start time.");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the date and time at which this event starts.
     *
     * @return This event's start date and time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the date and time at which this event ends.
     *
     * @return This event's end date and time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns whether another event has the same description and time range. */
    @Override
    boolean hasSameDetails(Task other) {
        if (!(other instanceof Event otherEvent)) {
            return false;
        }
        return super.hasSameDetails(other)
                && from.equals(otherEvent.from)
                && to.equals(otherEvent.to);
    }

    /**
     * Returns whether this event's inclusive date range contains the supplied date.
     *
     * @param date Date to compare with the event's range.
     * @return {@code true} if the event includes the date, otherwise {@code false}.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        boolean isOnOrAfterStart = !date.isBefore(from.toLocalDate());
        boolean isOnOrBeforeEnd = !date.isAfter(to.toLocalDate());
        return isOnOrAfterStart && isOnOrBeforeEnd;
    }

    /**
     * Returns the event in the format shown to users.
     *
     * @return A display string containing the task status, description, and time range.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + from.format(OUTPUT_FORMAT) + " to: " + to.format(OUTPUT_FORMAT) + ")";
    }

    /**
     * Returns the event in the format used by the storage file.
     *
     * @return A pipe-delimited event record.
     */
    @Override
    public String toFileString() {
        return appendCompletionDate("E | " + getCompletionState() + " | "
                + escapeStorageField(getDescription()) + " | " + from + " | " + to);
    }
}
