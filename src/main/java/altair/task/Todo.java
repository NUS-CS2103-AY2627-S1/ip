package altair.task;

/**
 * A task without an attached date or time.
 */
public class Todo extends Task {

    /**
     * Creates an unfinished ToDo task.
     *
     * @param description the task description.
     */
    public Todo(String description) {
        super(description);
    }
}
