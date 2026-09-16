package mira.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A task that should be completed by a stated date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description human-readable task description.
     * @param by due date.
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Returns the due date used for storage and date calculations.
     *
     * @return the due date.
     */
    public LocalDate getBy() {
        return by;
    }

    /** {@inheritDoc} */
    @Override
    protected String getDetails() {
        return " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
