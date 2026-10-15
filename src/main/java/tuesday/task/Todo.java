package tuesday.task;

/**
 * Represents a task without a deadline or event time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task.
     *
     * @param name the task description.
     */
    public Todo(String name) {
        super(name);
    }

    /**
     * Returns the task in its saved display format.
     *
     * @return the formatted todo task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
