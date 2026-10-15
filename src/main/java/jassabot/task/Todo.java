package jassabot.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task.
     *
     * @param description Work represented by this task.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toDataString() {
        return super.toDataString("T");
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
