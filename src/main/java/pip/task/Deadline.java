package pip.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private final LocalDate deadline;

    /**
     * Creates a new, incomplete deadline with the given name and due date.
     *
     * @param name The task's description.
     * @param deadline The date by which the task must be completed.
     */
    public Deadline(String name, LocalDate deadline) {
        super(name);
        this.deadline = deadline;
    }

    /**
     * Returns the date by which this task must be completed.
     *
     * @return The deadline date.
     */
    public LocalDate getDeadline() {
        return this.deadline;
    }

    /**
     * Returns this deadline's display representation, prefixed with the
     * {@code [D]} type tag and including the formatted due date, e.g.
     * {@code "[D][ ] submit report (by: Mar 15 2024)"}.
     *
     * @return The formatted display string.
     */
    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), this.deadline.format(DISPLAY_DATE_FORMAT));
    }
}
