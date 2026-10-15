package peanutbuttercat;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo in the format shown to users.
     *
     * @return A display string identifying this task as a todo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
