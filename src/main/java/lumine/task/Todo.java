package lumine.task;

/**
 * Represents a basic task with only a description and no associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates a new to-do task with the given description.
     *
     * @param description the task description (must not be blank).
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

}
