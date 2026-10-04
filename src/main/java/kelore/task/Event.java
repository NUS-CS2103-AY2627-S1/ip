package kelore.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import kelore.storage.Storage;

/** Represents a task that occurs between specified start and end times. */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter
            .ofPattern("MMM d uuuu, h:mm a", Locale.ENGLISH);
    /** Date and time when this event starts. */
    protected LocalDateTime from;
    /** Date and time when this event ends. */
    protected LocalDateTime to;

    /**
     * Creates an event with its description, start, and end.
     *
     * @param description Description of the event.
     * @param from Date and time when the event starts.
     * @param to Date and time when the event ends.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        assert from != null : "An event must have a start date and time";
        assert to != null : "An event must have an end date and time";
        assert to.isAfter(from) : "An event must end after it starts";
        this.from = from;
        this.to = to;
    }

    /** Returns a displayable description of this event and its completion status. */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }

    /** Returns this event in the format used by the data file. */
    @Override
    public String toStorageString() {
        return Storage.joinFields("E", getStorageStatus(), description,
                from.toString(), to.toString());
    }

    /**
     * Returns whether any part of this event occurs on the specified calendar date.
     *
     * @param date Calendar date to check.
     * @return True if the event occurs on the date; false otherwise.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = from.toLocalDate();
        LocalDate endDate = to.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /** Returns whether another event has the same description, start, and end times. */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && from.equals(((Event) other).from)
                && to.equals(((Event) other).to);
    }

    /** Returns an independent copy of this event. */
    @Override
    Event copy() {
        Event copiedEvent = new Event(description, from, to);
        if (isDone) {
            copiedEvent.markAsDone();
        }
        return copiedEvent;
    }
}
