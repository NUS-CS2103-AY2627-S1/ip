package mira.task;

/**
 * A task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo.
     *
     * @param description human-readable task description.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
