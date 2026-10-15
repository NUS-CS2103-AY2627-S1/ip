package duncan.task;

/**
 * Represents a task with no date/time attached, e.g. "borrow book".
 */
public class Todo extends Task {
    /**
     * Creates a todo that is not done yet.
     *
     * @param description What the task is, as typed by the user.
     */
    public Todo(String description) {
        super(description);
    }

    /** Returns this todo as a file line, e.g. "T\t0\tread book". */
    @Override
    public String toFileFormat() {
        return "T" + FIELD_SEPARATOR + getSharedFileFields();
    }

    /** Returns this todo for display, e.g. "[T][ ] read book". */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
