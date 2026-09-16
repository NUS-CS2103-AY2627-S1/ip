package alice;

/**
 * Represents a simple task without any date or time constraints.
 * Inherits from the Task class.
 */
public class ToDos extends Task {
    /**
     * Constructs a Todo task with the given description.
     *
     * @param description The task description.
     */
    public ToDos(String description) {
        super(description);
    }

    /**
     * {@inheritDoc}
     * Returns the type icon for a Todo task, which is "T".
     */
    @Override
    public String getTaskTypeIcon() {
        return "T";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task copy() {
        ToDos copy = new ToDos(description);
        copy.isDone = isDone;
        return copy;
    }

    /**
     * Returns the string representation of the todo task.
     * The format is "[T][status] description".
     *
     * @return The formatted string.
     */
    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
