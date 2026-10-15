package kojispawn.task;

/**
 * Represents a task with no attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    Task copy() {
        Todo copy = new Todo(description);
        copyCompletionStatusTo(copy);
        return copy;
    }

    @Override
    public String toDataString() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
