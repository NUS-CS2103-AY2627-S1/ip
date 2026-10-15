package koara.task;

/**
 * Represents a task without any date or time information.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(TaskType.TODO, description, "");
    }
}
