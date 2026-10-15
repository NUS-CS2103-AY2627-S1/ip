package elora.task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Represents a task that spans from a start time to an end time. The
 * start/end are free text (e.g. "Mon 2pm"), but when they happen to be
 * plain ISO dates (yyyy-mm-dd), the event also participates in date-based
 * features like {@code on} and {@code sort}, using from as its start date
 * and [from, to] as the range of dates it's considered to occur on.
 */
public class Event extends Task {
    protected String from;
    protected String to;
    private final LocalDate fromDate;
    private final LocalDate toDate;

    /**
     * Creates a new event with the given description, start, and end.
     *
     * @param description What the event is.
     * @param from When the event starts.
     * @param to When the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
        this.fromDate = tryParseIsoDate(from);
        this.toDate = tryParseIsoDate(to);
    }

    /**
     * Parses text as a plain ISO date (yyyy-mm-dd), returning null instead
     * of throwing if it isn't one, since from/to are usually free text.
     *
     * @param text The text to try to parse.
     * @return The parsed date, or null if text isn't a valid ISO date.
     */
    private static LocalDate tryParseIsoDate(String text) {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Returns true if this event's start and end are both ISO dates and
     * the given date falls within that range, inclusive. Always false if
     * either side is free text (not a parseable date).
     *
     * @param date The date to check against.
     * @return true if this event's [from, to] range includes date.
     */
    @Override
    public boolean isOccurringOn(LocalDate date) {
        if (fromDate == null || toDate == null) {
            return false;
        }
        return !date.isBefore(fromDate) && !date.isAfter(toDate);
    }

    /**
     * Returns this event's start date, used to sort it chronologically
     * against other tasks, or null if from isn't a parseable ISO date.
     *
     * @return This event's start date, or null if it has none.
     */
    @Override
    public LocalDate getSortDate() {
        return fromDate;
    }

    /**
     * Returns this event's representation for the save file, as
     * "E | &lt;base task format&gt; | &lt;from&gt; | &lt;to&gt;".
     *
     * @return The save-file line for this event.
     */
    @Override
    public String toSaveFormat() {
        return "E | " + super.toSaveFormat() + " | " + from + " | " + to;
    }

    /**
     * Returns this event as "[E]&lt;base task format&gt; (from:
     * &lt;from&gt; to: &lt;to&gt;)".
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns true if {@code other} is also an Event with the same
     * description, start, and end, so that adding a second event with
     * identical details can be flagged as a duplicate.
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        Event that = (Event) other;
        return from.equals(that.from) && to.equals(that.to);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}: equal
     * events (same description, from, and to) always hash the same.
     *
     * @return This event's hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from, to);
    }
}
