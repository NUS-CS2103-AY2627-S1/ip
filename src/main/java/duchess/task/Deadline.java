package duchess.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that must be completed by a specified date. */
public class Deadline extends Task {
    /** Format used when displaying a deadline to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** The deadline stored as an actual Java date rather than plain text. */
    private LocalDate by;

    /**
     * Creates an unfinished deadline task by parsing an ISO date.
     *
     * @param description the task description
     * @param by the deadline in {@code yyyy-MM-dd} format
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        assert by != null : "A deadline must have a date value";
        this.by = LocalDate.parse(by);
    }

    /**
     * Returns the deadline so it can be persisted in machine-readable form.
     *
     * @return the deadline date
     */
    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns the formatted deadline display with its type and status.
     *
     * @return the formatted deadline task
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
