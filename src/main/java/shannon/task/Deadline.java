package shannon.task;

import java.time.LocalDate;

import shannon.exception.InvalidDateException;

/**
 * A task that must be done before a specific date, e.g. {@code submit report by 2026-08-09}.
 * <p>
 * The date is kept as a {@link LocalDate} rather than as the text the user typed, so it can be
 * displayed in a friendlier format than it was entered in, and so later features such as sorting
 * become possible without re-reading the text.
 */
// LocalDate, not LocalDateTime: the commands accept a date only, and storing a time we never ask
// for would be inventing information.
public class Deadline extends Task {

    /** The letter that marks a deadline in the save file. */
    public static final String TYPE_CODE = "D";

    /** The date the task is due by. */
    private final LocalDate by;

    /**
     * Creates a deadline that is not yet done.
     *
     * @param description what needs to be done
     * @param by          the date it is due by, already parsed by {@link #parseBy(String)}
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "Deadline date must not be null";
        this.by = by;
    }

    /**
     * Turns the text after {@code /by} into a date.
     * <p>
     * The command handler and the save-file loader both come through here, so they agree on
     * exactly which dates are acceptable instead of each having their own copy of the rule.
     *
     * @param text the date as typed, expected in {@code yyyy-mm-dd} form.
     * @return the date it names.
     * @throws InvalidDateException if the text is not a date in that form, or names a day that
     *                              does not exist, such as {@code 2026-02-30}.
     */
    public static LocalDate parseBy(String text) throws InvalidDateException {
        return DateUtil.parseDate(text.trim());
    }

    /** Renders as {@code [D][X] submit report (by: Aug 09 2026)}. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DateUtil.DATE_DISPLAY_FORMAT) + ")";
    }

    /**
     * Renders as {@code D | 0 | submit report | 2026-08-09}.
     * The date is written in the input format, not the display format, so that a saved line can
     * be read back by the same {@link #parseBy(String)} the user's typing goes through.
     */
    @Override
    public String toFileFormat() {
        return encode(TYPE_CODE) + " | " + escape(by.format(DateUtil.DATE_INPUT_FORMAT));
    }

    /** {@inheritDoc} For a deadline, the due date must match too. */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && other instanceof Deadline deadline && by.equals(deadline.by);
    }
}
