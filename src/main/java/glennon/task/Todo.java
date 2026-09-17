package glennon.task;

/**
 * Represents a task that has no date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a pending to-do task with the given description.
     *
     * @param description description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the task with Glennon's to-do type marker and completion status.
     *
     * @return display-ready text such as {@code [T][ ] borrow book}.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
