package oreo.model;


/**
 * Represents a task with no associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description The task description.
     */
    public Todo(String description) {
        super(description);
    }

    /** Returns the to-do task category. */
    @Override
    public TaskType getTaskType() {
        return TaskType.TODO;
    }

}
