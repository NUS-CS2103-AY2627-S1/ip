package lenzabot.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description Description of what the task involves.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
