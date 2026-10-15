package gnaix.task;

/**
 * Represents a todo task without a deadline or event period.
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
     * Returns the todo task with its task type indicator.
     *
     * @return String representation of the todo task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
