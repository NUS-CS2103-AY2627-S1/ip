package dennis.task;

import java.time.LocalDate;
import java.util.Objects;

import dennis.DennisException;

/**
 * A task that spans a start and end date, shown as
 * {@code [E][ ] ... (from: Dec 02 2019 to: Dec 05 2019)}.
 *
 * <p>{@code from} equal to {@code to} (a one-day event) is allowed; only a
 * start strictly after the end is rejected, since that is almost certainly a
 * mistake rather than a deliberate state.</p>
 */
public class Event extends Task {
    /** Start date of the event. Stored as a real date, not free text. */
    private final LocalDate from;
    /** End date of the event. */
    private final LocalDate to;

    /**
     * Creates an event with the given description and start/end dates.
     *
     * @param description the task text
     * @param from        the start date in {@code yyyy-MM-dd} form
     * @param to          the end date in {@code yyyy-MM-dd} form
     * @throws DennisException if the description is blank or contains
     *                         {@code '|'}, either date is blank or not a
     *                         valid {@code yyyy-MM-dd} date, or {@code from}
     *                         is after {@code to}
     */
    public Event(String description, String from, String to)
            throws DennisException {
        super(validateDescription(description));
        this.from = validateFrom(from);
        this.to = validateTo(to);
        requireNotInverted(this.from, this.to);
    }

    /**
     * Checks the description and returns it unchanged when acceptable.
     *
     * @throws DennisException if it is blank or contains a {@code '|'}
     */
    private static String validateDescription(String description)
            throws DennisException {
        if (description.isBlank()) {
            throw new DennisException(
                    "I'm sorry, event must contain a task.");
        }

        return rejectSeparator(description, "A task description");
    }

    /**
     * Checks the start-date text and parses it into a {@link LocalDate}.
     *
     * @throws DennisException if {@code from} is blank or not a valid
     *                         {@code yyyy-MM-dd} date
     */
    private static LocalDate validateFrom(String from)
            throws DennisException {
        if (from.isBlank()) {
            throw new DennisException(
                    "The start of an event cannot be empty.");
        }

        return parseDate(from, "An event start");
    }

    /**
     * Checks the end-date text and parses it into a {@link LocalDate}.
     *
     * @throws DennisException if {@code to} is blank or not a valid
     *                         {@code yyyy-MM-dd} date
     */
    private static LocalDate validateTo(String to)
            throws DennisException {
        if (to.isBlank()) {
            throw new DennisException(
                    "The end of an event cannot be empty.");
        }

        return parseDate(to, "An event end");
    }

    /**
     * Checks that the event is not inverted. {@code from} equal to
     * {@code to} (a one-day event) is allowed; only a start strictly after
     * the end is rejected.
     *
     * @throws DennisException if {@code from} is after {@code to}
     */
    private static void requireNotInverted(LocalDate from, LocalDate to)
            throws DennisException {
        if (from.isAfter(to)) {
            throw new DennisException(
                    "The event's start date must not be after its end date.");
        }
    }

    @Override
    public String toFileFormat() {
        // from/to are written as ISO yyyy-MM-dd, the form parseDate accepts.
        return "E | " + getStatusNumber() + " | " + description
                + " | " + from + " | " + to;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        // Inclusive on both ends: an event counts on its start and end dates
        // and every day in between.
        return !date.isBefore(from) && !date.isAfter(to);
    }

    /**
     * Two events are equal when they have the same description, start date
     * and end date; completion status does not count, so this is what
     * {@link TaskList#add} uses to reject an exact duplicate.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Event other)) {
            return false;
        }
        return description.equals(other.description)
                && from.equals(other.from) && to.equals(other.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Event.class, description, from, to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + formatDate(from) + " to: " + formatDate(to) + ")";
    }
}
