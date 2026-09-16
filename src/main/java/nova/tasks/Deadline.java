package nova.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed before a specific deadline.
 */
public class Deadline extends Task {

    private LocalDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description description of the task
     * @param by deadline date and time
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        DateTimeFormatter outputFormat =
                DateTimeFormatter.ofPattern(
                        "MMM dd yyyy, h:mm a",
                        Locale.ENGLISH);

        return "[D]" + super.toString()
                + " (by: " + by.format(outputFormat) + ")";
    }

    @Override
    public String toFileString() {
        return "D | " + (isDone ? "X" : "O")
                + " | " + description
                + " | " + by;
    }
}
