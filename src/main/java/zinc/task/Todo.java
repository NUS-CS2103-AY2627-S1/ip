package zinc.task;

/** Represents a task with a description and no deadline. */
public class Todo extends Task {
    /**
     * Creates a todo task.
     *
     * @param description The description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the category of this task.
     *
     * @return The todo task type.
     */
    @Override
    public TaskType getTaskType() {
        return TaskType.TODO;
    }
}
