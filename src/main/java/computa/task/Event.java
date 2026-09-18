package computa.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import computa.util.DateTimeParser;

/**
 * A task with a specified start and end date or time.
 */
public class Event extends Task {
    /** Date or time when the event starts. */
    protected LocalDateTime from;
    /** Date or time when the event ends. */
    protected LocalDateTime to;

    /** Original start text for free-form values. */
    private final String fromText;
    /** Whether the parsed start value includes a time. */
    private final boolean fromHasTime;
    /** Original end text for free-form values. */
    private final String toText;
    /** Whether the parsed end value includes a time. */
    private final boolean toHasTime;

    /**
     * Creates an incomplete event task.
     *
     * @param description text describing the event.
     * @param from date or time when the event starts.
     * @param to date or time when the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.fromText = from.trim();
        this.toText = to.trim();
        this.from = DateTimeParser.parse(this.fromText);
        this.to = DateTimeParser.parse(this.toText);
        this.fromHasTime = DateTimeParser.hasTime(this.fromText);
        this.toHasTime = DateTimeParser.hasTime(this.toText);
    }

    /**
     * Returns this event in the format used by the task data file.
     *
     * @return the event type, status, description, and date/time range.
     */
    @Override
    public String toFileFormat() {
        return "E" + super.toFileFormat() + " | " + getStorageFrom() + " | " + getStorageTo();
    }


    /**
     * Formats this event for display in a task list.
     *
     * @return type icon, completion icon, description, and event range.
     */
    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + getDisplayDescription();
    }

    /** Returns the description together with the event range. */
    @Override
    public String getDisplayDescription() {
        return description + " (from: " + getFrom() + " to: " + getTo() + ")";
    }

    /** Returns whether another event has the same start and end date or time. */
    @Override
    protected boolean hasSameSchedule(Task other) {
        Event otherEvent = (Event) other;
        return getFrom().equals(otherEvent.getFrom()) && getTo().equals(otherEvent.getTo());
    }

    /** Returns the event start text. */
    public String getFrom() {
        return from == null ? fromText : DateTimeParser.formatForDisplay(from, fromHasTime);
    }

    /** Returns the event end text. */
    public String getTo() {
        return to == null ? toText : DateTimeParser.formatForDisplay(to, toHasTime);
    }

    /** Returns whether this event includes the supplied date, inclusively. */
    @Override
    public boolean occursOn(LocalDate date) {
        return from != null && to != null
                && !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }

    /** Returns the parsed start date used when sorting tasks. */
    @Override
    public LocalDateTime getSortDate() {
        return from;
    }

    /** Returns the normalized start value used when writing this event to disk. */
    private String getStorageFrom() {
        return from == null ? fromText : DateTimeParser.formatForStorage(from, fromHasTime);
    }

    /** Returns the normalized end value used when writing this event to disk. */
    private String getStorageTo() {
        return to == null ? toText : DateTimeParser.formatForStorage(to, toHasTime);
    }

    /** Returns the event type icon. */
    @Override
    public String getTypeIcon() {
        return "E";
    }
}
