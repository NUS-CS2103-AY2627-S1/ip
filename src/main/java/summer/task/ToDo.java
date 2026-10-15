package summer.task;

/**
 * Represents a task without any date or time attached to it.
 */
public class ToDo extends Task {
    /**
     * Creates a todo task with the given description and completion status.
     *
     * @param description text describing the task
     * @param isDone whether the task is already completed
     */
    public ToDo(String description, boolean isDone) {
        super(TaskType.TODO, description, isDone);
    }
}
