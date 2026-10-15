package bloop.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates a to-do task with the given description.
     *
     * @param description description of the task
     */
    public Todo(String description) {
        super(description);
    }

    /** Creates a to-do task with the given description and priority. */
    public Todo(String description, Priority priority) {
        super(description, priority);
    }

    /** Returns a display representation prefixed with the to-do type icon. */
    @Override
    public String toString() {
        return appendPriority("[T][" + getStatusIcon() + "] " + description);
    }
}
