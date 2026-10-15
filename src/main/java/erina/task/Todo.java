package erina.task;

/**
 * A task with nothing but a description, shown as {@code [T][ ] borrow book}.
 */
public class Todo extends Task {
    /**
     * Creates a to-do that is not yet done.
     *
     * @param description what the user wants to do
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String getTypeCode() {
        return "T";
    }
}
