package nob.task;

/**
 * Represents a task that does not have a date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description Text describing the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this to-do in the format used when displaying a task list.
     *
     * @return the task type, status, and description
     */
    @Override
    public String toString() {
        return "[T] " + super.toString();
    }
}
