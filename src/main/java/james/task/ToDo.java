package james.task;

/**
 * Represents a todo task without any date/time attached.
 */
public class ToDo extends Task {
    private static final String TODO_STORAGE_PREFIX = "T | ";
    private static final String TODO_DISPLAY_PREFIX = "[T]";


    /**
     * Initializes an uncompleted todo task with the given description.
     *
     * @param description Description of the todo.
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public Task copy() {
        Task copy = new ToDo(description);
        copy.isDone = isDone;
        return copy;
    }

    /**
     * Returns the string representation of the todo task formatted for file storage.
     *
     * @return Formatted todo string for storage.
     */
    @Override
    public String toFileString() {
        return TODO_STORAGE_PREFIX + super.toFileString();
    }

    /**
     * Returns the string representation of the todo task for display.
     *
     * @return Formatted display string of the todo task.
     */
    @Override
    public String toString() {
        return TODO_DISPLAY_PREFIX + super.toString();
    }
}
