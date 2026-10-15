package et.task;

/**
 * Represents a task without any date or time information.
 */
public class Todo extends Task {
    /**
     * Creates a ToDo task that is not done yet.
     *
     * @param description the text describing the task
     */
    public Todo(String description) {
        super(TaskType.TODO, description);
    }
}
