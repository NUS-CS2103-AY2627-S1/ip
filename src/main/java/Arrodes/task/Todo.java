package arrodes.task;

/** A task without a deadline or event interval. */
public class Todo extends Task {

    /**
     * Creates a todo with the supplied description.
     * @param description text describing the task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo with its type prefix and completion status.
     * @return formatted todo text
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
