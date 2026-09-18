package davidgoggins.task;

/** A task with only a description and no date attached. */
public class Todo extends Task {

    /**
     * Creates a todo that is not done yet.
     *
     * @param description the task's text
     */
    public Todo(String description) {
        super(description);
    }

    /** Shown as {@code [T][X] read book}, where {@code X} means done. */
    @Override
    public String toString() {
        return "[T]" + getStatusIcon() + description;
    }

    /** Saved as {@code T | 1 | read book}. */
    @Override
    public String toSaveFormat() {
        return toSaveFormat("T");
    }
}
