package allmind.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** A task that must be completed by a specific date. */
public class Deadline extends Task {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    /** The deadline date, stored as a strongly typed LocalDate. */
    protected LocalDate by;

    /**
     * Creates a new {@code Deadline} object
     * @param description Description of the task
     * @param by Date task should be done by
     * */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "A deadline must have a date";
        this.by = by;
    }

    /** Returns the date by which this task must be completed. */
    public LocalDate getBy() {
        return by;
    }

    @Override
    public String toString() {
        String displayedDate = by.format(DISPLAY_FORMAT);

        return "[D]" + super.toString() + " (by: " + displayedDate + ")";
    }
}
