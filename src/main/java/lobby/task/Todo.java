package lobby.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {

    /**
     * Creates a to-do that is initially incomplete.
     *
     * @param description the task description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Formats this to-do as one line in the save file.
     *
     * @return the serialized to-do
     */
    @Override
    public String toDataString() {
        return "T | " + toDataFields();
    }

    /**
     * Formats this to-do for display in the task list.
     *
     * @return the task type, completion status, and description
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
