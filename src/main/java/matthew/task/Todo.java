package matthew.task;

/** Represents a task without a deadline or event time range. */
public class Todo extends Task {

    /**
     * Creates an incomplete todo with the specified description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /** Returns the todo formatted with its type and completion status. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
