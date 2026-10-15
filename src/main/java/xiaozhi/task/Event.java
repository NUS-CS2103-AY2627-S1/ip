package xiaozhi.task;

import java.time.LocalDate;
import java.util.Objects;

import xiaozhi.util.Dates;

/**
 * A task that spans from one date to another.
 */
public class Event extends Task {
    protected LocalDate fromDate;
    protected LocalDate toDate;

    /**
     * Creates an event spanning the given dates.
     *
     * @param description What the event is.
     * @param fromDate Date the event starts.
     * @param toDate Date the event ends.
     */
    public Event(String description, LocalDate fromDate, LocalDate toDate) {
        super(description);
        assert fromDate != null && toDate != null
                : "Event's from/to dates should never be null; both are always the result of "
                + "Dates.parse(), which either returns a valid date or throws.";
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + " | " + fromDate + " | " + toDate;
    }

    /**
     * Returns whether {@code other} is an Event with the same description,
     * {@code /from} date and {@code /to} date as this one. Completion status
     * is not part of the comparison; used by {@link xiaozhi.command.AddCommand}
     * to reject adding the same event twice.
     *
     * @param other The object to compare against.
     * @return {@code true} if {@code other} is an equivalent Event, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Event)) {
            return false;
        }
        Event that = (Event) other;
        return description.equals(that.description)
                && fromDate.equals(that.fromDate)
                && toDate.equals(that.toDate);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getTypeIcon(), description, fromDate, toDate);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + Dates.format(fromDate) + " to: " + Dates.format(toDate) + ")";
    }
}
