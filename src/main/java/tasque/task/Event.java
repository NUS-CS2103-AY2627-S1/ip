package tasque.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * A task that occurs between specified start and end dates.
 */
public class Event extends Task {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d yyyy");

    private final LocalDate from;
    private final LocalDate to;
    private final String legacyFrom;
    private final String legacyTo;

    /**
     * Creates an event from ISO-8601 start and end dates.
     *
     * @param description Description of the task.
     * @param from Start date in ISO-8601 format.
     * @param to End date in ISO-8601 format.
     * @throws DateTimeParseException If either date is not a valid ISO-8601 date.
     */
    public Event(String description, String from, String to) {
        this(description, LocalDate.parse(from), LocalDate.parse(to), null, null);
    }

    private Event(String description, LocalDate from, LocalDate to,
            String legacyFrom, String legacyTo) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
        this.legacyFrom = legacyFrom;
        this.legacyTo = legacyTo;
        assert (this.from != null && this.to != null
                && this.legacyFrom == null && this.legacyTo == null)
                || (this.from == null && this.to == null
                && this.legacyFrom != null && this.legacyTo != null)
                : "Event must contain either two dates or two legacy values";
    }

    /**
     * Restores an event while retaining compatibility with older free-text event records.
     *
     * @param description Description of the event.
     * @param from Stored start value.
     * @param to Stored end value.
     * @return Restored event.
     */
    public static Event fromStoredValues(String description, String from, String to) {
        try {
            return new Event(description, from, to);
        } catch (DateTimeParseException e) {
            return new Event(description, null, null, from, to);
        }
    }

    /**
     * Checks whether this event has parsed dates with its end before its start.
     * Legacy text values are not treated as comparable dates.
     *
     * @return Whether the parsed end date precedes the start date.
     */
    public boolean hasReversedDates() {
        return this.from != null && this.to != null && this.to.isBefore(this.from);
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return other instanceof Event event && super.hasSameDetails(other)
                && Objects.equals(this.from, event.from) && Objects.equals(this.to, event.to)
                && Objects.equals(this.legacyFrom, event.legacyFrom)
                && Objects.equals(this.legacyTo, event.legacyTo);
    }

    /**
     * Returns this event in the task storage format with its time range.
     *
     * @return Storage record including the event's start and end.
     */
    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + getStoredFrom() + " | " + getStoredTo();
    }

    /**
     * Returns this event in the user-facing display format.
     *
     * @return Task display text including the event's start and end.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + getDisplayedFrom()
                + " to: " + getDisplayedTo() + ")";
    }

    private String getDisplayedFrom() {
        return this.from == null ? this.legacyFrom : this.from.format(DATE_FORMATTER);
    }

    private String getDisplayedTo() {
        return this.to == null ? this.legacyTo : this.to.format(DATE_FORMATTER);
    }

    private String getStoredFrom() {
        return this.from == null ? this.legacyFrom : this.from.toString();
    }

    private String getStoredTo() {
        return this.to == null ? this.legacyTo : this.to.toString();
    }
}
