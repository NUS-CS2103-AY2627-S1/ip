package avon.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an unfinished todo task.
     *
     * @param description the text describing the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo task with its type and completion status.
     *
     * @return the formatted todo task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
