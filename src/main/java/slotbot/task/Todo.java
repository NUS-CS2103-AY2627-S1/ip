package slotbot.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {

    /**
     * Creates a todo.
     *
     * @param description Todo description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo's display text.
     *
     * @return Todo type, status, and description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
