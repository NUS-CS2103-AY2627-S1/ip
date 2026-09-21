package caitlyn;

/**
 * A task that does not have a date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a new incomplete Todo task.
     *
     * @param description the text describing the task.
     */
    public Todo(String description) {
        super(description);
    }
}
