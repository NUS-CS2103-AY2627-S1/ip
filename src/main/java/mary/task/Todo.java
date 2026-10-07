package mary.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates an unfinished todo task.
     *
     * @param description task text.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Formats the task for console display with its type and completion markers.
     *
     * @return task description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
