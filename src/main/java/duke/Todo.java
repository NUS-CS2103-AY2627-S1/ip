package duke;

/**
 * A task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description Task text.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
