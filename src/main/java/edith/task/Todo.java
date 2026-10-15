package edith.task;

/** Represents a task with no associated date or time. */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description the text describing the todo
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo in the format used by the task list.
     *
     * @return the todo type, status icon, and description
     */
    @Override
    public String toString() {
        return toStorageString() + getTagSuffix();
    }

    @Override
    public String toStorageString() {
        return "[T]" + getStatusIcon() + " " + getDescription();
    }

    /**
     * Returns the identifier for a todo task.
     *
     * @return {@code todo}
     */
    @Override
    protected String getTaskType() {
        return "todo";
    }
}
