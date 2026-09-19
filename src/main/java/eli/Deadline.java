package eli;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates a deadline task.
     *
     * @param description the task description
     * @param by the deadline text entered by the user
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = TaskDateTimeFormatter.formatDateOrDateTime(by);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }

    @Override
    Optional<LocalDateTime> getSortDate() {
        return TaskDateTimeFormatter.parseDisplayDateOrDateTime(by);
    }

    @Override
    boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && by.equalsIgnoreCase(((Deadline) other).by);
    }
}
