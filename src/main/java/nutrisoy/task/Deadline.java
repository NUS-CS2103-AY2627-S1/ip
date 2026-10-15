package nutrisoy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task with a due date.
 */
public class Deadline extends Task {
    protected LocalDate by;

    /**
     * Creates a deadline task with its description and due date.
     *
     * @param description description of the task
     * @param by due date of the task
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "Deadline date must not be null";
        this.by = by;
    }

    /**
     * Returns this deadline in the storage-file format.
     *
     * @return storage-file representation of this deadline
     */
    @Override
    public String toFileFormat() {
        return "D | " + getCoreFileFormat() + " | " + by + getTagsFileSuffix();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && by.equals(((Deadline) other).by);
    }

    /**
     * Returns a display representation of this deadline.
     *
     * @return display representation of this deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ")";
    }
}
