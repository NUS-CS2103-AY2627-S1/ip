package seedu.goat.task;

/**
 * A task with nothing but a description, such as {@code todo borrow book}.
 */
public class Todo extends Task {

    /**
     * Creates a to-do that starts out not done.
     *
     * @param description what the user wants to do.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toFileString() {
        return "T | " + getFileStatus() + " | " + description;
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
