package luna.task;

/**
 * Represents a todo task without date or time information.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description Description of the todo task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the user-facing text representation of this todo task.
     *
     * @return Formatted todo text for display.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
