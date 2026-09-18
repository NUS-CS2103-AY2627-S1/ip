package bobby.task;

import java.time.LocalDateTime;

import bobby.util.DateTimeParser;

/**
 * Represents a task that should be completed by a specific date or time.
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description text that describes the task.
     * @param by deadline date/time.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        assert by != null : "Deadline date should be non-null.";
        this.by = by;
    }

    /**
     * Returns this deadline in the chatbot's list display format.
     *
     * @return deadline type, status, description, and deadline.
     */
    @Override
    public String toString() {
        return baseDisplayString() + " (by: " + DateTimeParser.formatForDisplay(by) + ")" + tagsDisplayString();
    }

    /**
     * Returns a line that can be saved in the data file.
     *
     * @return serialized deadline data.
     */
    @Override
    public String toStorageString() {
        return typeAndStatusStorageString() + " | " + getDescription() + " | " + by + tagsStorageString();
    }

    /**
     * Returns a stable key used to detect duplicate deadlines.
     *
     * @return deadline type, description, and due date/time.
     */
    @Override
    public String getIdentityKey() {
        return super.getIdentityKey() + " | " + by;
    }
}
