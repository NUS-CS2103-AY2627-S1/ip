package remy.task;

/**
 * Represents a task without a deadline or event period.
 */
public class Todo extends Task {

    /**
     * Creates a to-do task with the given description.
     *
     * @param description Description supplied by the user.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this to-do task's display representation.
     *
     * @return A string representing this task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
