package dennis.task;

import java.time.LocalDate;
import java.util.Objects;

import dennis.DennisException;

/**
 * A task that must be completed sometime within a start and end date, shown
 * as {@code [W][ ] ... (within: Jan 15 2019 to: Jan 25 2019)}.
 *
 * <p>Unlike {@link Event}, whose inverted range is merely harmless (it just
 * matches no date in {@link #occursOn}), an inverted window here is rejected
 * outright: a task that must be done "within" a period whose end is before
 * its start is almost certainly a mistake rather than a deliberate state.</p>
 */
public class WithinPeriodTask extends Task {
    /** Start of the period. Stored as a real date, not free text. */
    private final LocalDate from;
    /** End of the period. */
    private final LocalDate to;

    /**
     * Creates a within-period task with the given description and window.
     *
     * @param description the task text
     * @param from        the window's start date in {@code yyyy-MM-dd} form
     * @param to          the window's end date in {@code yyyy-MM-dd} form
     * @throws DennisException if the description is blank or contains
     *                         {@code '|'}, either date is blank or not a
     *                         valid {@code yyyy-MM-dd} date, or {@code from}
     *                         is after {@code to}
     */
    public WithinPeriodTask(String description, String from, String to)
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
                    "I'm sorry, within must contain a task.");
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
                    "The start of the period cannot be empty.");
        }

        return parseDate(from, "A period start");
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
                    "The end of the period cannot be empty.");
        }

        return parseDate(to, "A period end");
    }

    /**
     * Checks that the window is not inverted. {@code from} equal to
     * {@code to} (a one-day window) is allowed; only a start strictly after
     * the end is rejected.
     *
     * @throws DennisException if {@code from} is after {@code to}
     */
    private static void requireNotInverted(LocalDate from, LocalDate to)
            throws DennisException {
        if (from.isAfter(to)) {
            throw new DennisException(
                    "The period's start date must not be after its end date.");
        }
    }

    @Override
    public String toFileFormat() {
        // from/to are written as ISO yyyy-MM-dd, the form parseDate accepts.
        return "W | " + getStatusNumber() + " | " + description
                + " | " + from + " | " + to;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        // Inclusive on both ends: the task counts on its window's start and
        // end dates and every day in between.
        return !date.isBefore(from) && !date.isAfter(to);
    }

    /**
     * Two within-period tasks are equal when they have the same description,
     * window start and window end; completion status does not count, so
     * this is what {@link TaskList#add} uses to reject an exact duplicate.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WithinPeriodTask other)) {
            return false;
        }
        return description.equals(other.description)
                && from.equals(other.from) && to.equals(other.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(WithinPeriodTask.class, description, from, to);
    }

    @Override
    public String toString() {
        return "[W]" + super.toString()
                + " (within: " + formatDate(from) + " to: " + formatDate(to) + ")";
    }
}
