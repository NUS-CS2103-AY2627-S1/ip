package notmarth.model;

/**
 * Represents a task that has no date or time attached to it.
 */
public class ToDo extends Task {
    /**
     * Creates an unfinished ToDo task.
     *
     * @param description the task description.
     */
    public ToDo(String description) {
        super(description, TaskType.TODO);
    }
}
