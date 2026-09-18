package littledaisy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import littledaisy.exception.LittleDaisyException;

/**
 * A task that has to be finished before a stated point in time, e.g.
 * {@code return book (by: Oct 15 2019)}.
 */
public class Deadline extends Task {
    /** Format accepted in a deadline command and stored on disk. */
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE;

    /** Human-readable format used when showing a deadline. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /** Calendar date by which the task has to be done. */
    private final LocalDate by;

    /**
     * Creates a deadline that is not done yet.
     *
     * @param description what the deadline is about
     * @param by calendar date by which it has to be done
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Creates a deadline from the date syntax accepted in user commands.
     *
     * @param description what the deadline is about
     * @param dateText date in {@code yyyy-MM-dd} format
     * @return parsed deadline
     * @throws LittleDaisyException if the date is absent or invalid
     */
    public static Deadline fromInput(String description, String dateText)
            throws LittleDaisyException {
        try {
            return new Deadline(description, LocalDate.parse(dateText, INPUT_FORMAT));
        } catch (DateTimeParseException e) {
            throw new LittleDaisyException(
                    "A deadline date should use yyyy-MM-dd, e.g. 2019-10-15.");
        }
    }

    /**
     * Returns the calendar date by which this task is due.
     *
     * @return due date
     */
    public LocalDate getBy() {
        return by;
    }

    /** Returns whether another deadline has the same description and date. */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && by.equals(((Deadline) other).by);
    }

    /**
     * Returns this deadline with its type icon and due time, e.g.
     * {@code [D][ ] return book (by: Sunday)}.
     *
     * @return the type icon, the inherited rendering, then the due time
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
