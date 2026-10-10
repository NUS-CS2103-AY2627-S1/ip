package zsiggy.task;

/**
 * Represents a todo task without an associated date.
 */
public class Todo extends Task {

    /**
     * Creates a todo task.
     *
     * @param description the description of the todo
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the textual representation of the todo.
     *
     * @return the formatted todo
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
