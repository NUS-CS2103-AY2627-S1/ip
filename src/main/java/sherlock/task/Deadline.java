package sherlock.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date.
 */
public class Deadline extends Task {
    /** Uses a fixed locale so deadline text is identical on every supported operating system. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.US);
    private final LocalDate deadlineDate;

    /**
     * Creates an incomplete task with a deadline date in ISO-8601 format.
     *
     * @param description text describing the task
     * @param deadlineDate deadline date in {@code yyyy-MM-dd} format
     */
    public Deadline(String description, String deadlineDate) {
        super(description);
        this.deadlineDate = LocalDate.parse(deadlineDate);
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return super.toString() + " (by: " + deadlineDate.format(DISPLAY_DATE_FORMAT) + ")";
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + deadlineDate;
    }
}
