package toothless.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo in the chatbot's display format.
     *
     * @return formatted todo
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
