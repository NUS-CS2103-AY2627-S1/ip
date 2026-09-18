package aether.task;

/**
 * A task with no date or time attached, for example {@code visit new theme park}.
 */
public class Todo extends Task {
    /**
     * Creates a todo that starts as not done.
     *
     * @param description what the user needs to do
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

    /**
     * Returns this todo in list form, for example {@code [T][ ] borrow book}.
     *
     * @return the type icon plus the shared task text
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "]" + super.toString();
    }
}
