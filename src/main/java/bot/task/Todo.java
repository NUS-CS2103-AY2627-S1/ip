package bot.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task.
     *
     * @param description description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo in the format used by the storage file.
     *
     * @return storage representation containing the todo task type
     */
    @Override
    public String toFileString() {
        return "T | " + super.toFileString();
    }

    /**
     * Returns the todo's type, status, and description for display.
     *
     * @return display representation of the todo
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
