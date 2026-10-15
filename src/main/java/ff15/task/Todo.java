package ff15.task;

/**
 * Represents a task with no date/time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a todo.
     *
     * @param description what the user typed to describe the task.
     */
    public Todo(String description) {
        super(description);
    }

    /** Returns this task with its type marker, e.g. {@code [T][ ] read book}. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /** Returns this task in save-file form, e.g. {@code T | 0 | read book}. */
    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }
}
