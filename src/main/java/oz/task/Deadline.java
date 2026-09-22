package oz.task;

import java.time.LocalDate;

/**
 * Represents a task with a specific deadline date or date-time.
 */
public class Deadline extends Task {
    /** Task type code used in storage and display output. */
    public static final String TYPE_CODE = "D";

    /** Target deadline date and time. */
    protected TaskDateTime deadline;

    /**
     * Constructs a Deadline task with description and deadline date/time.
     *
     * @param description Description of the deadline task.
     * @param deadline Date or time by which the task should be completed.
     */
    public Deadline(String description, TaskDateTime deadline) {
        super(description);
        assert deadline != null : "A deadline must have a date-time";
        this.deadline = deadline;
    }

    /**
     * Checks if this deadline occurs on the given date.
     *
     * @param date The date to check against.
     * @return True if the deadline falls on the specified date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return this.deadline.toLocalDate().equals(date);
    }

    /**
     * Converts the deadline task into a file storage representation.
     *
     * @return Formatted string for writing to file.
     */
    @Override
    public String toFileFormat() {
        return String.format(TYPE_CODE + " | %s | %s", super.toFileFormat(), this.deadline.toStorageString());
    }

    @Override
    public String toString() {
        return String.format("[" + TYPE_CODE + "][%s] %s (by: %s)",
                super.getStatusIcon(), this.description, this.deadline.toDisplayString());
    }
}
