package nelson.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import nelson.exception.NelsonException;

/**
 * Represents a task that occurs between a start and end time.
 */
public class Event extends Task {
    /** The format used when displaying event dates to the user. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    /** The format used when writing event dates to storage. */
    private static final DateTimeFormatter STORAGE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    /** The event start date. */
    protected LocalDate from;
    /** The event end date. */
    protected LocalDate to;

    /**
     * Creates an incomplete event task.
     *
     * @param description the text describing the task
     * @param from the event start date in yyyy-mm-dd format
     * @param to the event end date in yyyy-mm-dd format
     * @throws NelsonException if either date is not in yyyy-mm-dd format
     */
    public Event(String description, String from, String to) throws NelsonException {
        super(description);
        try {
            this.from = LocalDate.parse(from);
            this.to = LocalDate.parse(to);
        } catch (DateTimeParseException exception) {
            throw new NelsonException("Molo! Invalid date. Please use the yyyy-mm-dd format.");
        }
    }

    /**
     * Returns this event in the format used for saving it to disk.
     *
     * @return the event type, status, description, start, and end separated by pipes
     */
    @Override
    public String toStorageString() {
        return "E | " + (isDone ? "1" : "0") + " | " + description + " | "
                + from.format(STORAGE_FORMAT) + " | " + to.format(STORAGE_FORMAT);
    }

    /**
     * Returns the event type, status, description, and time range.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}
