package lizzy.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo.
     *
     * @param description the text describing the todo
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Restores a todo with its saved completion state.
     *
     * @param description the text describing the todo
     * @param isDone whether the todo has been completed
     */
    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    /**
     * Returns this todo with its type marker.
     *
     * @return the todo's display-ready representation
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
