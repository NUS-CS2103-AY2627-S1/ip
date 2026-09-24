package sage.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that must be completed by a specified date and time. */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy, hh:mma", Locale.ENGLISH);

    private final LocalDateTime by;

    /** Creates a deadline with its description and due date.
     *
     * @param description deadline description
     * @param by date and time by which the deadline must be completed
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /** Returns the date and time by which this task should be completed.
     *
     * @return deadline date and time
     */
    public LocalDateTime getBy() {
        return by;
    }

    /** Returns this deadline in Sage's display format. */
    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + by.format(DISPLAY_FORMATTER) + ")";
    }
}
