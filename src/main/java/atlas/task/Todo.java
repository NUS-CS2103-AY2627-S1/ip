package atlas.task;

/** Represents a task without a deadline or event time. */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description text describing the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo in todo display format.
     *
     * @return the todo type marker followed by the task details.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
