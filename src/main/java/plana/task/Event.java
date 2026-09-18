package plana.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import plana.storage.Storage;

/**
 * Represents a task that starts and ends on specified dates.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an incomplete Event.
     *
     * @param description the task description.
     * @param from the start date.
     * @param to the end date.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        // The event parser and storage loader supply both boundary dates before
        // construction, so date-range operations can safely use both fields.
        assert from != null && to != null : "An event must have start and end dates.";
        if (from == null || to == null || !from.isBefore(to)) {
            throw new IllegalArgumentException("An event's start date must be before its end date.");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the Event in the format used by the storage file.
     *
     * @return the Event type, completion status, description, start, and end
     */
    @Override
    public String toStorageString() {
        return "E | " + getStorageStatus() + " | " + Storage.escapeField(description)
                + " | " + Storage.escapeField(from.toString()) + " | " + Storage.escapeField(to.toString());
    }

    /**
     * Returns the Event in the format used when displaying the task list.
     *
     * @return the Event type, completion status, description, start, and end
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Checks whether this event is happening on the supplied date, including
     * either endpoint of the event's date range.
     *
     * @param date the date to check.
     * @return {@code true} if the event includes the date
     */
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    /**
     * Returns a key made from this event's identifying details.
     *
     * @return the task type, description, start date, and end date key.
     */
    @Override
    protected String getDetailsKey() {
        return "E\u0000" + description + "\u0000" + from + "\u0000" + to;
    }
}
