package bott.task;

/**
 * Represents a task with no date or time attached to it.
 */
public class Todo extends Task {

    /**
     * Creates a new todo task with the given description.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
