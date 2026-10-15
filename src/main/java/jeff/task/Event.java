package jeff.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * A task that starts on a specific date and ends on a specific date,
 * e.g. "team project meeting from 2019-10-02 to 2019-10-06".
 */
public class Event extends Task {
    private static final DateTimeFormatter PRINT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    protected LocalDate from;
    protected LocalDate to;

    /**
     * Creates a new, not-done event with the given description and start/end dates.
     *
     * @param description what the task is; must not be null or blank.
     * @param from the start date, in "yyyy-mm-dd" format; must not be null, blank, or wrongly formatted.
     * @param to the end date, in "yyyy-mm-dd" format; must not be null, blank, or wrongly formatted.
     * @throws IllegalArgumentException if description or either date is invalid, or if
     *     {@code from} is after {@code to}.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = parseDate(from, "/from");
        this.to = parseDate(to, "/to");
        if (this.from.isAfter(this.to)) {
            throw new IllegalArgumentException("An event's /from date can't be after its /to date.");
        }
    }

    /**
     * Parses a "yyyy-mm-dd" date string, wrapping a blank or wrongly
     * formatted value as an {@link IllegalArgumentException} so callers can
     * handle it the same way as any other invalid task field. {@code label}
     * (e.g. "/from") identifies which argument failed, for the error message.
     */
    private static LocalDate parseDate(String date, String label) {
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("An event needs a " + label + " date.");
        }
        try {
            return LocalDate.parse(date.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Please give the " + label + " date in yyyy-mm-dd format, e.g. 2019-12-02.");
        }
    }

    /** Returns this event's status icon, description, and from/to dates, prefixed with "[E]". */
    @Override
    public String toString() {
        // Same reasoning as Deadline.toString(): from and to are each set exactly once,
        // in the constructor, by parseDate(), which either returns a valid LocalDate or
        // throws before this object finishes constructing. So both should always be
        // non-null on a fully constructed Event.
        assert from != null && to != null : "from/to should never be null on a fully constructed Event";
        return "[E]" + super.toString() + " (from: " + from.format(PRINT_FORMAT)
                + " to: " + to.format(PRINT_FORMAT) + ")";
    }

    /** Returns this event's start date, since that's what an event should be sorted by. */
    @Override
    public Optional<LocalDate> getChronologicalDate() {
        return Optional.of(from);
    }

    /** Returns this event's data encoded for {@link jeff.storage.Storage}, prefixed with "E". */
    @Override
    public String toSaveFormat() {
        return "E | " + super.toSaveFormat() + " | " + from + " | " + to;
    }
}
