package marcus.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this to-do in its user-facing display format.
     *
     * @return to-do type, status, and description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toStringWithoutTags() {
        return "[T]" + super.toStringWithoutTags();
    }
}
