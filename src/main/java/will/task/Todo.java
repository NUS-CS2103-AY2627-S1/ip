package will.task;

/** A task with just a description and no associated date, e.g. "read book". */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description What needs to be done.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
