package anders.task;

/**
 * Represents a task without a deadline or a scheduled time.
 */
public class Todo extends Task {

    /**
     * Creates a new unfinished todo task.
     *
     * @param description the todo task description
     */
    public Todo(String description) {
        super(description);
    }

    /** Returns the display text for this todo task. */
    @Override
    public String toString() {
        return "[T] " + super.toString() + getTagsDisplayText();
    }
}
