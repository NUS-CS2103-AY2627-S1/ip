package quackers;

/**
 * Represents a task with no associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete to-do task.
     *
     * @param description the task description.
     */
    public Todo(String description) {
        super(TaskType.TODO, description);
    }
}
