package baron.core.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private final LocalDateTime deadline;

    /**
     * Creates a deadline task with the specified description and deadline.
     *
     * @param description The task description.
     * @param deadline The time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime deadline) {
        this(UUID.randomUUID(), description, deadline);
    }

    /**
     * Creates a deadline task with the specified identifier, description, and deadline.
     *
     * @param uuid The task identifier.
     * @param description The task description.
     * @param deadline The time by which the task must be completed.
     */
    public Deadline(UUID uuid, String description, LocalDateTime deadline) {
        super(uuid, TaskType.DEADLINE, description);
        assert deadline != null : "Deadline tasks must have a deadline";
        this.deadline = deadline;
    }

    /**
     * Returns this deadline task in the persistent storage format.
     *
     * @return The persistent deadline-task representation.
     */
    @Override
    public String toFileString() {
        return String.join(" | ", super.toFileString(), deadline.toString());
    }

    /**
     * Returns a user-facing representation of this deadline task.
     *
     * @return The formatted deadline task.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a, d MMM yyyy");
        return super.toString() + String.format("\nby: %s", deadline.format(formatter));
    }
}
