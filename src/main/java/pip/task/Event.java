package pip.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that spans a start date and an end date.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private final LocalDate start;
    private final LocalDate end;

    /**
     * Creates a new, incomplete event with the given name and date range.
     *
     * @param name The task's description.
     * @param start The event's start date.
     * @param end The event's end date.
     * @throws IllegalArgumentException If either date is {@code null}, or if
     *         {@code end} is before {@code start}.
     */
    public Event(String name, LocalDate start, LocalDate end) {
        super(name);
        if (start == null || end == null || end.isBefore(start)) {
            throw new IllegalArgumentException("Event end date must not be before its start date.");
        }
        this.start = start;
        this.end = end;
        assert this.start != null && this.end != null && !this.end.isBefore(this.start)
                : "An event must have an ordered, non-null date range";
    }

    /**
     * Returns this event's start date.
     *
     * @return The start date.
     */
    public LocalDate getStart() {
        return this.start;
    }

    /**
     * Returns this event's end date.
     *
     * @return The end date.
     */
    public LocalDate getEnd() {
        return this.end;
    }

    /**
     * Returns this event's display representation, prefixed with the
     * {@code [E]} type tag and including the formatted date range, e.g.
     * {@code "[E][ ] team trip (from: May 01 2024, to: May 10 2024)"}.
     *
     * @return The formatted display string.
     */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s, to: %s)", super.toString(),
                this.start.format(DISPLAY_DATE_FORMAT), this.end.format(DISPLAY_DATE_FORMAT));
    }
}
