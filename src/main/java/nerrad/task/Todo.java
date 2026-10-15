package nerrad.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo in its user-facing display format.
     *
     * @return Todo type icon followed by the common task display.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}

