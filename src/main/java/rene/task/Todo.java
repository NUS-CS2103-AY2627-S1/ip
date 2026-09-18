package rene.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description the text describing the task.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.TODO;
    }

    @Override
    Task copy() {
        Todo copy = new Todo(getDescription());
        if (isDone()) {
            copy.markAsDone();
        }
        return copy;
    }
}
