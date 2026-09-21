package sel.task;

/**
 * Represents a simple task with no associated date or time.
 */
public class ToDo extends Task {

    /**
     * Creates a new, not-done todo task with the given description.
     *
     * @param description text describing the task.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns the display form of this todo, e.g. {@code "[T][X] read book"}.
     *
     * @return the formatted string representation of this todo.
     */
    @Override
    public String toString() {
        return "[T][" + this.getStatusIcon() + "] " + this.description;
    }
}
