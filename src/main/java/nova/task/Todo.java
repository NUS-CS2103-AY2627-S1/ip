package nova.task;

/** Represents a todo task. */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description the text describing the task
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
