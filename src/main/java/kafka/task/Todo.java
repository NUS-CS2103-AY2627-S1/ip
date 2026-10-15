package kafka.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an unfinished todo.
     *
     * @param description Work the user wants Kafka to remember.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Copies a todo for an edit that has not yet been saved.
     */
    private Todo(Todo source) {
        super(source);
    }

    @Override
    Task copy() {
        return new Todo(this);
    }

    /**
     * Adds the todo marker to the common task display.
     *
     * @return Display text beginning with {@code [T]}.
     */
    @Override
    public String display() {
        return "[T]" + super.display();
    }

    /**
     * Serializes this todo for the task data file.
     *
     * @return Todo data in Kafka's storage format.
     */
    @Override
    public String toDataString() {
        return super.toDataString("T");
    }
}
