package bingus.task;

/**
 * Represents a task with no associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description description of the todo task.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

}
