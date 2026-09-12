package bob.task;

/**
 * Represents a task without an associated date or time.
 */
public class ToDo extends Task {

    /**
     * Creates a to-do task with the specified description.
     *
     * @param description description of the task.
     */
    public ToDo(String description) {
        super(description, TaskType.TODO);
    }
}
