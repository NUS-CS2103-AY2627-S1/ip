package nubish.tasks;

/**
 * Represents a simple task without date or time information.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task with the given description.
     *
     * @param description description of the todo task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Creates a todo task with the given completion status and description.
     *
     * @param isDone whether the todo task has been completed
     * @param description description of the todo task
     */
    public Todo(boolean isDone, String description) {
        super(isDone, description);
    }

    /**
     * Returns the user-facing representation of this todo task.
     *
     * @return formatted todo string with type, status, and description
     */
    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }

    /**
     * Returns the storage representation of this todo task.
     *
     * @return formatted todo string suitable for saving to disk
     */

    @Override
    public String saveString() {
        return String.format("T | %s", super.saveString());
    }
}
