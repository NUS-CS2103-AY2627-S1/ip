package tardt.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import tardt.exception.TardTException;
import tardt.priority.Priority;

/**
 * A type of Task that has a deadline associated with it.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm",
            Locale.ENGLISH);
    private final LocalDateTime by;

    /**
     * Constructor for Deadline task.
     * @param description Description for Deadline task.
     * @param by Deadline of the Deadline task.
     * @throws TardTException Unique exception of the TardT class
     */
    public Deadline(String description, String by) throws TardTException {
        this(description, by, Priority.LOW);
    }

    /** Creates a deadline task with the given priority. */
    public Deadline(String description, String by, Priority priority) throws TardTException {
        super(description, priority);
        try {
            this.by = DateTimeParser.parse(by);
        } catch (DateTimeParseException e) {
            throw new TardTException(DateTimeParser.FORMAT_ERROR);
        }
    }

    /**
     * Returns the deadline as a string in a different format as the expected input.
     *
     * @return A string represent datetime in MMM dd yyyy time.
     */
    public String getBy() {
        return by.format(DISPLAY_FORMAT);
    }

    /**
     * Outputs the date-time of Task deadline as a String.
     * Leaves it in one of the ISO-8601 formats.
     *
     * @return The date and time of the deadline of the Task as a String unchanged.
     */
    public String getByRaw() {
        return by.toString();
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.getBy() + ")";
    }
}
