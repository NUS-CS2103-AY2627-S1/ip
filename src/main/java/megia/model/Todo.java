package megia.model;

/**
 * Represents a task without a deadline or scheduled time range.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the specified description.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Restores a todo task with its description and completion status.
     *
     * @param description Description of the task.
     * @param isDone Completion status of the task.
     */
    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String encode() {
        return TaskType.TODO.name() + ',' + super.encode();
    }
}
