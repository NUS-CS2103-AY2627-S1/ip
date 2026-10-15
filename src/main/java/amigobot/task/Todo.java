package amigobot.task;

/**
 * Represents a task without any date/time attached.
 */
public class Todo extends Task {

    /**
     * Constructs a new Todo with the given description.
     *
     * @param description the description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns a string representation prefixed with [T], e.g. "[T][ ] read book".
     *
     * @return the formatted todo string.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
