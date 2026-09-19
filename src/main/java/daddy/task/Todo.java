package daddy.task;

/**
 * A task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task that is initially not done.
     *
     * @param description the todo description
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
