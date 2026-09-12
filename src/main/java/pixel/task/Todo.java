package pixel.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates a todo with the specified description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public Task withDescription(String newDescription) {
        return preserveStatus(new Todo(newDescription));
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
