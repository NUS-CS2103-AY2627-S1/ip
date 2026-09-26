package dulio.task;

/**
 * A task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task.
     *
     * @param description the task description.
     */
    public Todo(String description) {
        super(description);
    }
}
