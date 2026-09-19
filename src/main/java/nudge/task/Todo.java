package nudge.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete todo with the specified description.
     *
     * @param description description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo formatted with its task type and completion status.
     *
     * @return formatted todo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
