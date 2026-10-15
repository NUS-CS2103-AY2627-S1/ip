package tungtung;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/** Represents a task that must be completed by a particular calendar date. */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    protected LocalDate by;

    /**
     * Creates a deadline task.
     *
     * @param description task description
     * @param by date by which the task must be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = Objects.requireNonNull(by, "Deadline date cannot be null.");
        assert this.by != null : "A deadline must have a date after construction.";
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
