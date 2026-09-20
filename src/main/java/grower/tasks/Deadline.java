package grower.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specified deadline.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a");

    private final LocalDateTime deadline;

    /**
     * Creates a deadline task with its due date and time.
     *
     * @param description Description of the task.
     * @param deadline Date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime deadline) {
        super(description);
        this.deadline = deadline;
    }

    @Override
    public String toFileString() {
        return String.format(
                "D | %d | %s | %s",
                isCompleted() ? 1 : 0,
                getDescription(),
                deadline.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    @Override
    public LocalDateTime getSortTime() {
        return deadline;
    }

    /**
     * Returns a display string containing the task and its deadline.
     *
     * @return Display representation of this deadline task.
     */
    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), deadline.format(DISPLAY_FORMATTER));
    }
}
