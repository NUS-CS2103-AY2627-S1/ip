package nexus;

/** A task without date or time information. */
public class Todo extends Task {
    /**
     * Creates a to-do task.
     *
     * @param description the task description.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
