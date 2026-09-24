package murphy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** A task that must be completed by a specified date. */
public class Deadline extends Task {
    /** The format used when displaying a deadline to the user. */
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** The date by which this task must be completed. */
    private final LocalDate by;

    /** Creates an incomplete deadline task. */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /** Returns whether this deadline falls on the supplied date. */
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    /** Returns whether another deadline has the same description and date. */
    @Override
    public boolean hasSameDetailsAs(Task other) {
        return other instanceof Deadline deadline
                && normalize(description).equals(normalize(deadline.description))
                && by.equals(deadline.by);
    }

    /** Returns this deadline in Murphy's save-file format. */
    @Override
    public String toDataString() {
        return "D | " + (isDone() ? "1" : "0") + " | " + escapeDataField(description)
                + " | " + escapeDataField(by.toString());
    }

    /** Returns the display text for this deadline. */
    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] " + description + " (by: "
                + by.format(DISPLAY_FORMATTER) + ")";
    }
}
