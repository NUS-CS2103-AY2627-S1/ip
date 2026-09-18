package cookie.task;

/** Represents the most basic task type, with no date or time attached. */
public class Todo extends Task {

    /**
     * Creates a todo task with the specified description.
     *
     * @param description The task description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the string for {@code Todo} tasks to be logged.
     *
     * @return The todo's storage representation.
     */
    @Override
    public String toFileFormat() {
        return formatForStorage(TaskType.TODO);
    }

    /**
     * Returns the todo's type marker, checkbox, and description.
     *
     * @return The todo's display representation.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
