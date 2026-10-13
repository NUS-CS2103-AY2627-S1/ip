package ruby.task;

import java.time.LocalDateTime;

import ruby.command.Parser;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final LocalDateTime deadline;

    /**
     * Creates a deadline with the specified description and due date and time.
     *
     * @param description Description of the deadline.
     * @param deadline    Due date and time.
     */
    public Deadline(String description, LocalDateTime deadline) {
        super(description);
        assert deadline != null : "Deadline cannot be null";
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "[" + TaskDataFormat.DEADLINE_TYPE + "]"
                + super.toString()
                + " (by: " + Parser.formatDateTime(deadline) + ")";
    }

    @Override
    public String toDataString() {
        return TaskDataFormat.DEADLINE_TYPE
                + TaskDataFormat.PART_SEPARATOR
                + super.toDataString()
                + TaskDataFormat.PART_SEPARATOR
                + deadline;
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return other instanceof Deadline
                && super.hasSameDetails(other)
                && deadline.equals(((Deadline) other).deadline);
    }
}
