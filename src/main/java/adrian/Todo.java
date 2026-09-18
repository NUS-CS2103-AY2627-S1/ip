package adrian;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description description of the task.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

    /**
     * Returns the todo in the format used for persistent storage.
     *
     * @return serialized todo data.
     */
    @Override
    public String toDataString() {
        return "T | " + super.toDataString();
    }
}
