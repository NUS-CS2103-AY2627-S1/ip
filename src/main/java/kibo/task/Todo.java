package kibo.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {

    /**
     * Creates a to-do task with the given description.
     *
     * @param description description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this task in the to-do display format.
     *
     * @return task text prefixed with the {@code [T]} type marker.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
