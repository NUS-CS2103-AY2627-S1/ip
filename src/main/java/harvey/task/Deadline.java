package harvey.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import harvey.HarveyException;
/**
 * A task that must be finished before a given date,
 * e.g. {@code return book (by: Oct 15 2019)}.
 * <p>
 * The due date is held as a {@link LocalDate} rather than as text, so it is a real date
 * the program understands rather than a string it merely repeats back. That is what makes
 * it possible to show the date in a friendlier format than the user typed it, and what
 * would later allow deadlines to be sorted or compared.
 */
public class Deadline extends Task {
    /**
     * How a date is shown to the user, e.g. {@code Oct 15 2019}.
     * Deliberately different from the input format, to make the point that the stored
     * value is a date and not the text that was typed.
     * <p>
     * The locale is fixed to English. Without it, the month name follows the language of
     * the computer, e.g. the Chinese for October on one set to Chinese, while every other
     * word Harvey says stays English.
     */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /** The format the user types and the save file uses, e.g. {@code 2019-10-15}. */
    private static final String INPUT_FORMAT = "yyyy-mm-dd";

    /** When the task is due. */
    protected LocalDate by;

    /**
     * Creates a deadline that is not done yet.
     *
     * @param description the text describing the task.
     * @param by          when the task is due.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * {@inheritDoc}
     * A deadline also needs the same due date: the same essay due on two dates is two tasks.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        // super checks the class first, so the cast below is safe once it has passed.
        return super.hasSameDetails(other) && by.equals(((Deadline) other).by);
    }

    /**
     * Turns typed text into a date.
     * <p>
     * This lives here, next to the field it produces, so that the one place that decides
     * what a valid date looks like is shared by both the command parser and the save file
     * reader instead of being written out twice.
     *
     * @param text the date as typed, expected in {@code yyyy-mm-dd} form.
     * @return the date it represents.
     * @throws HarveyException if the text is not a date in that form.
     */
    public static LocalDate parseDate(String text) throws HarveyException {
        try {
            // LocalDate.parse expects exactly this format, so no formatter is needed here.
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            // DateTimeParseException is Java's complaint; it is replaced with advice the
            // user can act on, and routed through the same channel as every other problem.
            throw new HarveyException("I could not read \"" + text + "\" as a date. "
                    + "Please write it as " + INPUT_FORMAT + ", for example 2019-10-15.");
        }
    }

    /**
     * {@inheritDoc}
     * Wraps the inherited form with the {@code [D]} marker and the due date in the display
     * format, giving e.g. {@code [D][ ] return book (by: Oct 15 2019)}.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * {@inheritDoc}
     * Prefixes the {@code D} type letter and appends the due date,
     * giving e.g. {@code D | 0 | return book | 2019-10-15}.
     * <p>
     * The date is written with {@code LocalDate.toString()}, which produces the same
     * {@code yyyy-mm-dd} form that {@link #parseDate(String)} reads, so the file can be
     * loaded back by the same code that reads what the user types. The display format is
     * deliberately not used here: it would have to be parsed differently on the way in.
     */
    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + by;
    }
}
