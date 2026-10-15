package chione.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import chione.DateTimes;

/**
 * A task that must be finished before a given date or time,
 * e.g. {@code "submit report /by 2019-10-15 1800"}.
 *
 * <p>Displayed as {@code [D][ ] submit report (by: Oct 15 2019, 6:00pm)}.
 */
public class Deadline extends Task {
    /** The letter that marks a saved line as a deadline. */
    public static final String TYPE_LETTER = "D";

    /**
     * When the task is due.
     *
     * <p>Held as a {@link LocalDateTime} rather than as text, so that the date
     * can be compared with other dates — which is what lets the {@code on}
     * command find every task falling on a particular day. Storing it as a
     * string would make that a matter of comparing spelling rather than time.
     */
    protected LocalDateTime by;

    /**
     * Creates a deadline that starts out not done.
     *
     * @param description what the user wants to do
     * @param by          when it is due
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        // Both callers get the moment from DateTimes.parse, which throws rather
        // than hand back null; this records that reliance.
        assert by != null : "a deadline with no moment has nothing to compare or show";
        this.by = by;
    }

    /** Reports whether this deadline falls on the given day. */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    /**
     * Reports whether this deadline repeats another task: same description,
     * and due at the same moment.
     *
     * <p>The class check in {@code super} guarantees the cast below is safe.
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        return super.isDuplicateOf(other) && by.equals(((Deadline) other).by);
    }

    /** Returns the task prefixed with its type icon and followed by the due date. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimes.format(by) + ")";
    }

    /**
     * Returns the saved line with the type letter in front and the due date
     * appended, e.g. {@code "D | 0 | return book | 2019-10-15 1800"}.
     */
    @Override
    public String toSaveFormat() {
        return TYPE_LETTER + SEPARATOR + super.toSaveFormat()
                + SEPARATOR + DateTimes.toSaveFormat(by);
    }
}
