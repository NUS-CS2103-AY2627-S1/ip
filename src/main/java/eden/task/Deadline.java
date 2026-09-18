package eden.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a given date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates an unmarked deadline task.
     *
     * @param description task description.
     * @param by date on which the task is due.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toDataString() {
        return "D | " + (isMarked() ? "1" : "0") + " | "
                + escapeDataField(getDescription()) + " | "
                + by.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + by.format(DISPLAY_DATE_FORMATTER) + ")";
    }
}
