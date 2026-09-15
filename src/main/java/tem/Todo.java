package tem;

/**
 * Represents a task with no associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete to-do task.
     *
     * @param description description supplied by the user.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

    /**
     * Returns this to-do in the on-disk format {@code T | done | description}.
     *
     * @return storage line for this to-do
     */
    @Override
    public String toStorageString() {
        return "T | " + (isDone() ? "1" : "0") + " | " + escapeStorageField(getDescription());
    }
}
