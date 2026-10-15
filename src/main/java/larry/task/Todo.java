package larry.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the task in the format used by Larry's responses.
     *
     * @return Status icon followed by the task description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
