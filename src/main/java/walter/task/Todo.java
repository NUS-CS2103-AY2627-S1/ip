package walter.task;

/**
 * Represents a task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description Description of the todo task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the Todo type marker followed by the base task display form.
     *
     * @return Display form such as {@code [T][ ] read book}.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
