package clsl.task;

import java.time.LocalDate;

/**
 * Represents a task that must be completed by a particular date.
 */
public class Deadline extends Task {
    private final LocalDate dueDate;
    private final String formattedDueDate;

    /**
     * Creates an incomplete deadline task.
     *
     * @param name Description of the task.
     * @param dueDate Due date in {@code yyyy-mm-dd} format.
     */
    public Deadline(String name, String dueDate) {
        super(name);
        this.dueDate = LocalDate.parse(dueDate);
        this.formattedDueDate = this.dueDate.format(DISPLAY_DATE_FORMAT);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formattedDueDate + ")";
    }

    /**
     * Returns this deadline in the storage format.
     *
     * @return The task prefixed with its deadline marker and due date.
     */
    @Override
    public String getSaveString() {
        return "D | " + super.getSaveString() + " | " + dueDate;
    }

    @Override
    public boolean isDuplicateOf(Task other) {
        return other instanceof Deadline
                && super.isDuplicateOf(other)
                && dueDate.equals(((Deadline) other).dueDate);
    }

    /**
     * Returns whether this deadline is due on the specified date.
     *
     * @param date Date to compare with the deadline.
     * @return {@code true} if the dates are equal, or {@code false} otherwise.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return dueDate.equals(date);
    }
}
