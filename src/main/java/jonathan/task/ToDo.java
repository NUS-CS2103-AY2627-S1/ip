package jonathan.task;

/** Represents a task without a date or time. */
public class ToDo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description text describing the task
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
