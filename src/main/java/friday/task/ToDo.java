package friday.task;

/**
 * A task with no date or time attached.
 */
public class ToDo extends Task {
    /**
     * Creates an incomplete task without a date or time.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns the todo type, completion marker, and description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
