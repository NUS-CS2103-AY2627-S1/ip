package augustus.task;

/**
 * Represents a todo task without a specific date or time.
 */
public class ToDo extends Task {

    /**
     * Creates a todo task with the given description.
     *
     * @param description Description of the todo task.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns the display representation of this todo task.
     *
     * @return String representation of the todo task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns the representation of this todo task used for storage.
     *
     * @return String representation of the todo task for saving to a file.
     */
    @Override
    public String toFileString() {
        return String.format("T | %s | %s%s",
                isDone() ? "1" : "0",
                getDescription(),
                getTagForStorage());
    }
}
