package duchess.tasks;

import java.time.LocalDate;

/**
 * Represents a task with a deadline.
 */
public class Deadline extends Task {
    private final LocalDate dueDate;

    /**
     * Creates a task with a deadline.
     *
     * @param description The description of the task.
     * @param dueDate The deadline of the task.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);

        assert (dueDate != null) : "A deadline must have a due date";

        this.dueDate = dueDate;
    }

    /**
     * Returns the string representation of a task with a deadline,
     * in user-readable format.
     */
    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)",
                super.toString(),
                this.dueDate.format(DateFormat.DISPLAY_FORMAT));
    }

    /**
     * Returns the string representation of a task with a deadline,
     * in storage format.
     */
    @Override
    public String getStorageFormat() {
        return String.format("%s | D | /by %s",
                super.getStorageFormat(),
                this.dueDate.format(DateFormat.PARSE_FORMAT));
    }
}
