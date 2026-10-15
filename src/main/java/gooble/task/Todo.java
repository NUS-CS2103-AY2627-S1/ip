package gooble.task;

/**
 * Represents a task without an associated date, time, or deadline.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task with the given description.
     *
     * @param description the text describing the to-do task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the formatted representation used for a to-do task.
     *
     * @return the task type, completion status, and description
     */
    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + description + formatTags();
    }
}
