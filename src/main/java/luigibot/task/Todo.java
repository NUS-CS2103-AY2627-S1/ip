package luigibot.task;

/**
 * Represents a task without a specific date or time.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo type icon followed by its task details.
     *
     * @return display form of this todo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
