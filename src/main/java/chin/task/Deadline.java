package chin.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import chin.util.ChinException;

/**
 * A task with an ISO {@link LocalDate} deadline, rendered as
 * {@code [D][ ] desc (by: MMM d yyyy)}.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");
    private final LocalDate by;

    /** Creates a deadline; {@code by} must be in {@code yyyy-MM-dd} form. */
    public Deadline(String description, String by) throws ChinException {
        super(description);
        try {
            this.by = LocalDate.parse(by);
        } catch (DateTimeParseException e) {
            throw new ChinException("OOPS!!! Deadline date must be in yyyy-MM-dd (e.g. 2019-10-15).");
        }
    }

    /** Returns the deadline date, used for sorting. */
    public LocalDate getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public String serialize() {
        return "D | " + super.serialize() + " | " + by.toString();
    }
}
