package clara.task;

/**
 * Represents a todo task without a specific deadline or time period.
 */
public class Todo extends Task {

    /**
     * Creates a todo task with the specified name.
     *
     * @param taskName the name of the task
     */
    public Todo(String taskName) {
        super(taskName);
    }

    /**
     * Returns a string representation of this todo task.
     *
     * @return a formatted string representing this todo task
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
