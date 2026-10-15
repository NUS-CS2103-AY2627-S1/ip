package orion.task;

import java.time.LocalDateTime;

/**
 * Represents a task that needs to be completed by a specific date and time.
 */
public class Deadline extends Task {
    protected LocalDateTime deadline;

    /**
     * Initializes a new Deadline task with the specified description and deadline.
     *
     * @param description Description of the task.
     * @param deadline Date and time of the deadline.
     */
    public Deadline(String description, LocalDateTime deadline) {
        super(description);
        this.deadline = deadline;
    }

    /**
     * Initializes a new Deadline task with the specified description, deadline, and completion status.
     *
     * @param description Description of the task.
     * @param deadline Date and time of the deadline.
     * @param isDone {@code true} if the task is completed, {@code false} otherwise.
     */
    public Deadline(String description, LocalDateTime deadline, boolean isDone) {
        super(description, isDone);
        this.deadline = deadline;
    }

    @Override
    public String getTypeIcon() {
        return "D";
    }

    @Override
    public String toFileFormat() {
        return super.toFileFormat() + " | " + deadline.format(SAVE_FORMAT);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + deadline.format(OUTPUT_FORMAT) + ")";
    }
}
