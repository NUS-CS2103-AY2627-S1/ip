package koko;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates a to-do task.
     *
     * @param description the text describing the task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this to-do in the task-list format.
     *
     * @return the formatted to-do
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
