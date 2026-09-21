package janet.task;

/**
 * Represents a to-do task without a date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a new incomplete to-do task.
     *
     * @param description the text describing the to-do.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the icon identifying this task as a to-do.
     *
     * @return the to-do type icon
     */
    @Override
    public String getTypeIcon() {
        return "[T]";
    }
}
