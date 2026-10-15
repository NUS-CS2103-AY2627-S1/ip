package zikiai.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo with its type and completion status.
     *
     * @return formatted todo for display
     */
    @Override
    public String getDescription() {
        return "[T]" + super.getDescription();
    }

    /**
     * Returns this todo in the format used by the data file.
     *
     * @return storage representation of this todo
     */
    @Override
    public String toDataString() {
        return "[T][" + getStatusIcon() + "]" + getPriorityStorageTag()
                + " | " + getDescriptionText();
    }
}
