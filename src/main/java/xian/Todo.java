package xian;

/**
 * Represents a simple task without any date or time attached to it.
 * A {@code Todo} is the simplest task type, consisting only of a description.
 */
public class Todo extends Task {

    /**
     * Creates a new Todo task with the given description.
     *
     * @param description the description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the string representation of this Todo task,
     * prefixed with "[T]" to indicate its type.
     *
     * @return the formatted string representation of this task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
