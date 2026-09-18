package meowmeow.task;

import java.time.LocalDate;

/**
 * Represents a task that must be completed by a specific date or time.
 */
public class Deadline extends Task {

    /** Date or time by which the task must be completed */
    protected TaskDateTime by;

    /**
     * Constructs a deadline with the given description and due date.
     *
     * @param description Description of the deadline.
     * @param by Date or time the task is due.
     */
    public Deadline(String description, TaskDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }

    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + by.toStorageFormat();
    }

    @Override
    public boolean isDuplicateOf(Task other) {
        return super.isDuplicateOf(other) && by.equals(((Deadline) other).by);
    }
}
