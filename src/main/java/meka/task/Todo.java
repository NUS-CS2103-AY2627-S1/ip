package meka.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {

    /**
     * Creates a todo task with the given description.
     *
     * @param description description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo task with its type and completion status.
     *
     * @return the task formatted as "[T][ ] description".
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
