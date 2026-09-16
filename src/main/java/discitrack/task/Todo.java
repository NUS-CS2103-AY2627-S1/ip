package discitrack.task;

/**
 * Represents a todo task with no date.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given activity.
     *
     * @param task the activity represented by the todo task.
     */
    public Todo(String task) {
        super(task);
    }

    /**
     * Returns the display text for this todo task.
     *
     * @return the todo task formatted for display.
     */
    @Override
    public String toString() {
        return "[T] " + super.toString();
    }
}
