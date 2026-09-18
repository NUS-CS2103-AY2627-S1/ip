package milo.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import milo.exception.MiloException;

/**
 * Represents a task that must be done before a specific date or time,
 * for example "submit report by 2026-10-15 1800".
 * <p>
 * The due date is held as a {@link LocalDateTime} rather than as text, so it
 * is a real point in time that could be compared or sorted, and so the format
 * it is displayed in is independent of the format it was typed in. The
 * formats it can be typed in live in {@link TaskDate}.
 */
public class Deadline extends Task {
    /** How a date with no time is shown to the user, e.g. "Oct 15 2026". */
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /** How a date with a time is shown, e.g. "Dec 2 2026, 6:00pm". */
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mma", Locale.ENGLISH);

    /** How a date with no time is written to the save file. */
    private static final DateTimeFormatter FILE_DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

    /** How a date with a time is written to the save file. */
    private static final DateTimeFormatter FILE_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm", Locale.ENGLISH);

    /** When the task is due. */
    protected LocalDateTime by;

    /**
     * Whether the user supplied a time of day. A date-only deadline is stored
     * at midnight, so this records the difference between "no time given" and
     * "due at 00:00", which would otherwise be indistinguishable.
     */
    protected boolean hasTime;

    /**
     * Creates a deadline, reading the due date from text.
     *
     * @param description what the task is about
     * @param by          when the task is due, in one of the accepted formats
     * @throws MiloException if the text is not a date Milo can understand
     */
    public Deadline(String description, String by) throws MiloException {
        super(description);

        String trimmed = by.trim();
        TaskDate.Parsed parsed = TaskDate.parse(trimmed).orElseThrow(() ->
                new MiloException("I couldn't make sense of \"" + trimmed + "\" as a date. "
                        + "Try 2026-10-15, or 2026-10-15 1800 to include a time "
                        + "(2/12/2026 and 2/12/2026 1800 work too)."));

        this.by = parsed.value();
        this.hasTime = parsed.hasTime();
    }

    /**
     * Creates a deadline from an existing one, taking its already-parsed due
     * date rather than reading it from text again. Kept private because it
     * exists only to serve {@link #copy()}, and because the public
     * constructor's job of interpreting user input does not apply here.
     *
     * @param other the deadline to copy
     */
    private Deadline(Deadline other) {
        super(other.description);
        this.by = other.by;
        this.hasTime = other.hasTime;
        this.isDone = other.isDone;
    }

    /**
     * Returns an independent copy of this deadline.
     *
     * @return a deadline equal to this one but sharing no state with it
     */
    @Override
    public Deadline copy() {
        return new Deadline(this);
    }

    /**
     * Returns the due date written the way it is shown to the user.
     *
     * @return e.g. "Oct 15 2026", or "Dec 2 2026, 6:00pm" when a time was given
     */
    private String getFormattedBy() {
        if (!hasTime) {
            return by.format(DISPLAY_DATE);
        }
        // The pattern yields "6:00PM"; lower case reads better mid-sentence.
        return by.format(DISPLAY_DATE_TIME).replace("AM", "am").replace("PM", "pm");
    }

    /**
     * Returns the deadline tagged with its type and due date, for example
     * {@code [D][ ] return book (by: Oct 15 2026)}.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + getFormattedBy() + ")";
    }

    /**
     * Returns this task as one line of the save file.
     * The date is written back in the yyyy-MM-dd form so that a saved file
     * always reloads, whichever format the user originally typed.
     */
    @Override
    public String toFileFormat() {
        String savedDate = hasTime ? by.format(FILE_DATE_TIME) : by.format(FILE_DATE);
        return "D" + FILE_SEPARATOR + super.toFileFormat() + FILE_SEPARATOR + savedDate;
    }
}
