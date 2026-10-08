package rudra.task;

/**
 * Represents a todo task without any time information.
 */
public class ToDo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description Description of the todo task.
     */
    public ToDo(String description) {
        super(TaskType.TODO, description);
    }
}
