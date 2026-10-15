package orion.task;

/**
 * Represents a task without any specific date or time constraints.
 */
public class Todo extends Task {

    /**
     * Initializes a new Todo task with the specified description.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Initializes a new Todo task with the specified description and completion status.
     *
     * @param description Description of the task.
     * @param isDone {@code true} if the task is completed, {@code false} otherwise.
     */
    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    @Override
    public String getTypeIcon() {
        return "T";
    }
}
