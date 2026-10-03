package verity.task;

import java.time.LocalDate;

/**
 * Represents a task without an associated date.
 */
public class Todo extends Task {

    /**
     * Creates a todo task.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns a user-facing summary of this todo.
     *
     * @return Formatted todo summary.
     */
    @Override
    public String getStatus() {
        return "[T]" + super.getStatus();
    }

    /**
     * Returns a line representing this todo in the data file.
     *
     * @return Tab-separated todo data.
     */
    @Override
    public String serialize() {
        return appendClientMetadata(
                String.join("\t", "T", getStorageStatus(), getDescription()));
    }

    /**
     * Returns whether this todo occurs on the specified date.
     * A todo has no associated date, so this method always returns false.
     *
     * @param date Date to check.
     * @return False.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return false;
    }
}
