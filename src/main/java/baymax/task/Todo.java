package baymax.task;

/**
 * Represents a task that does not have an associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates a new unfinished todo task.
     *
     * @param description the text describing the task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo type marker followed by the task details.
     *
     * @return the formatted todo task
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns the todo fields in the format used when saving tasks.
     *
     * @return the task type, completion status, and description
     */
    @Override
    public String toStorageString() {
        return "T" + " | " + super.toStorageString();
    }
}
