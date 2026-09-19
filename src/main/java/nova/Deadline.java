package nova;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be done by a certain date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates a deadline with the given description and due date.
     *
     * @param description the deadline description
     * @param by the due date
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the due date of this deadline.
     *
     * @return the due date
     */
    public LocalDate getBy() {
        return this.by;
    }

    /**
     * Returns a display string of this deadline, e.g. "[D][ ] return book (by: Oct 15 2019)".
     *
     * @return the display string
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.by.format(OUTPUT_FORMAT) + ")";
    }
}
