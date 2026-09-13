package yappy.task;

/**
 * Represents a task without any date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     */
    public Todo(String description) {
        super(TaskType.TODO, description);
    }

    @Override
    public String toDataString() {
        return "T | " + (isDone ? "1" : "0") + " | " + encode(description);
    }
}
