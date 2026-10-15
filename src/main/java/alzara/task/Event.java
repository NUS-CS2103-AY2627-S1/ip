package alzara.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * A task with a description, a done/not-done state, and a start/end date range.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private LocalDate start;
    private LocalDate end;

    /**
     * Creates an event that starts out not done.
     *
     * @param description the task's description
     * @param start the date the event starts
     * @param end the date the event ends
     */
    public Event(String description, LocalDate start, LocalDate end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * Returns this task's save-file line, prefixed with the type letter and
     * suffixed with both dates.
     *
     * @return the save-file line, e.g. {@code "E | N | trip | 2019-10-15 | 2019-10-16"}
     */
    @Override
    public String toSaveFormat() {
        return "E | " + super.toSaveFormat() + " | " + this.start + " | " + this.end;
    }

    /**
     * Returns this task's console display, prefixed with the type letter and
     * suffixed with both dates.
     *
     * @return the display line, with both dates as {@code "(from: MMM dd yyyy to: MMM dd yyyy)"}
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.start.format(DISPLAY_FORMAT)
                + " to: " + this.end.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns true if {@code date} falls within {@code [start, end]} inclusive.
     *
     * <p>{@code start} is guaranteed not to be after {@code end}: every
     * {@link Event} in the app is constructed either by {@link
     * alzara.parser.CommandParser#parse}, which rejects a new {@code event}
     * command whose {@code /from} date is after its {@code /to} date, or by
     * {@link alzara.storage.Storage#load}, which skips a save-file entry
     * with the same problem as corrupted - so this range is never empty.
     *
     * @param date the queried date
     * @return true if this event is happening on {@code date}
     */
    @Override
    public boolean isScheduledOn(LocalDate date) {
        return !date.isBefore(this.start) && !date.isAfter(this.end);
    }

    @Override
    public LocalDate getSortDate() {
        return this.start;
    }

    /**
     * Returns true if {@code other} is an {@link Event} with the same
     * description (per {@link Task#equals}) and the same start/end dates.
     * Used to detect duplicate tasks when adding a new one.
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        Event event = (Event) other;
        return this.start.equals(event.start) && this.end.equals(event.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), start, end);
    }
}
