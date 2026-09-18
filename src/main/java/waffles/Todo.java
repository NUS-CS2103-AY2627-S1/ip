package waffles;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates a new todo task.
     *
     * @param description the text describing the task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo in the format used by the task list.
     *
     * @return the todo type marker and shared task representation
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
