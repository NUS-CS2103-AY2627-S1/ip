package lily.task;

/** Represents a task without a date or time. */
public class ToDo extends Task {
    /**
     * Creates a to-do task with the given description.
     *
     * @param description description of the task
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
