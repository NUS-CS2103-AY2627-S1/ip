package kiki.task;

/**
 * Represents a task with a description only, and no associated date/time.
 */
public class ToDos extends Task {

    /**
     * Creates a new todo task.
     *
     * @param description Description of the task.
     */
    public ToDos(String description) {
        super(description, TaskType.TODO);
    }

}
