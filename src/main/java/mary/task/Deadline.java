package mary.task;

import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    protected LocalDateTime by;

    /**
     * Creates an unfinished deadline task.
     *
     * @param description task text.
     * @param by local deadline date/time.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Formats the task for console display with its type and completion markers.
     *
     * @return task description and formatted date/time details.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formatDateTime(by) + ")";
    }

    /**
     * Serializes the type, completion flag, description, and ISO date/time fields.
     *
     * @return pipe-delimited record for storage; descriptions are not escaped.
     */
    @Override
    public String toStorageRecord() {
        return "D | " + (isDone ? "1" : "0") + " | " + description + " | " + by;
    }

    /**
     * Returns the deadline date/time.
     *
     * @return the local due date/time.
     */
    public LocalDateTime getBy() {
        return by;
    }
}
