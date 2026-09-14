package cookie.task;

import java.time.LocalDate;

/** Represents a task with a deadline. */
public class Deadline extends Task {

    /** Stores the date or time by which the task should be completed. */
    private final DateTimeValue by;

    /**
     * Creates a deadline task with the specified description and deadline.
     *
     * @param description The task description.
     * @param by The date or time by which the task should be completed.
     */
    public Deadline(String description, DateTimeValue by) {
        super(description);
        assert by != null : "A deadline must have a date or time";
        this.by = by;
    }

    /**
     * Returns the deadline's date or time.
     *
     * @return The deadline date or time.
     */
    public DateTimeValue getBy() {
        return this.by;
    }

    /** Returns whether this deadline is due on the requested date. */
    @Override
    public boolean occursOn(LocalDate date) {
        return date.equals(by.getDate());
    }

    /** Returns whether another deadline has the same due value. */
    @Override
    protected boolean hasSameTypeSpecificDetails(Task other) {
        return by.equals(((Deadline) other).by);
    }

    /**
     * Returns the string for {@code Deadline} tasks to be logged.
     *
     * @return The deadline's storage representation.
     */
    @Override
    public String toFileFormat() {
        return formatForStorage(TaskType.DEADLINE) + " | " + by.toStorageString();
    }

    /**
     * Returns the deadline's type marker, checkbox, description, and deadline.
     *
     * @return The deadline's display representation.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.toDisplayString() + ")";
    }
}
