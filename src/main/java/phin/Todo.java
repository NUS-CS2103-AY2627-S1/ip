package phin;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates a to-do task that has not been completed yet.
     *
     * @param description description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Formats this task with the to-do type indicator.
     *
     * @return the formatted to-do task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
