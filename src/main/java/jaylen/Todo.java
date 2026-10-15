package jaylen;

/** A task without an attached date or time. */
public class Todo extends Task {
    /**
     * Creates a ToDo with the supplied description.
     *
     * @param description the task description
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
