package minty.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * Represents a task that takes place between a start and an end date.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an incomplete event.
     *
     * @param description description of the event.
     * @param from date on which the event starts.
     * @param to date on which the event ends.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        assert from != null : "An event start date must be validated before construction";
        assert to != null : "An event end date must be validated before construction";
        assert !to.isBefore(from)
                : "An event end date must not precede its validated start date";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event in the format used by the save file.
     *
     * @return serialized event.
     */
    @Override
    public String toDataString() {
        return "E | " + (isDone ? "1" : "0") + " | "
                + escapeDataField(description) + " | " + from + " | " + to;
    }

    /**
     * Checks whether this event takes place on the specified date.
     *
     * @param date date to check.
     * @return {@code true} if the date falls within this event's date range, inclusive.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    @Override
    public Optional<LocalDate> getReminderDate() {
        return Optional.of(from);
    }

    /**
     * Returns the event in Minty's display format.
     *
     * @return task status and description followed by the event's date range.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_DATE_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
