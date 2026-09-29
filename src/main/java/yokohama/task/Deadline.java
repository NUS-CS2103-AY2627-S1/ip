package yokohama.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a specified date and time.
 */
public class Deadline extends Todo {
    private final LocalDateTime deadline;

    /**
     * Creates a deadline task with the specified description, completion state, and deadline.
     *
     * @param description Description of the task.
     * @param isDone Whether the task is complete.
     * @param deadline Date and time by which the task should be completed.
     */
    public Deadline(String description, boolean isDone, LocalDateTime deadline) {
        super(description, isDone);
        if (deadline == null) {
            throw new IllegalArgumentException("Deadline date and time are required.");
        }
        this.deadline = deadline;
    }

    @Override
    public String toDbString() {
        return String.format("D | %s | %s | %s \n", getStorageCompletionValue(), getDescription(), deadline);
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return deadline.toLocalDate().equals(date);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formatForUser(deadline) + ")";
    }
}
