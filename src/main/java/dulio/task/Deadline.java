package dulio.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private LocalDate by;

    /**
     * Creates a deadline task.
     *
     * @param description The task description.
     * @param by The date by which the task should be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "Deadline date must not be null";
        this.by = by;
    }

    @Override
    public String getTypeIcon() {
        return "D";
    }

    public LocalDate getBy() {
        return by;
    }

    /**
     * Returns the task description together with its deadline.
     *
     * @return The formatted deadline.
     */
    @Override
    public String toString() {
        String formattedDate = by.format(DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH));
        return description + " (by: " + formattedDate + ")";
    }
}
