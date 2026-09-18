package habi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description Text describing the deadline.
     * @param by Due date in ISO format.
     */
    public Deadline(String description, String by) {
        this(description, LocalDate.parse(by));
    }

    /**
     * Creates an incomplete deadline with a parsed date.
     *
     * @param description Text describing the deadline.
     * @param by Parsed due date.
     */
    public Deadline(String description, LocalDate by) {
        super(TaskType.DEADLINE, description);
        this.by = by;
    }

    /**
     * Returns the parsed deadline date.
     *
     * @return Deadline value.
     */
    public LocalDate getBy() {
        return by;
    }

    /** Returns the formatted due-date details shown after the description. */
    @Override
    protected String getTimingDetails() {
        return " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    /** Returns this deadline in the persistent storage format. */
    @Override
    public String toDataString() {
        return super.toDataString() + "\t" + by;
    }
}
