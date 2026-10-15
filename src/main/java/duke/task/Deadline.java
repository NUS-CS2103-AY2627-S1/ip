package duke.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that needs to be done by a specific date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates a new deadline task.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "Deadline date should not be null";
        this.by = by;
    }

    /**
     * Returns the date or time by which this deadline should be done.
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns this deadline in the format shown to the user.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
