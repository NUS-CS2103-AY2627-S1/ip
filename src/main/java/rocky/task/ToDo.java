package rocky.task;

/** A task with no date attached, e.g. "read book". */
public class ToDo extends Task {

    /**
     * Creates a todo, initially not marked done.
     *
     * @param description what the task is, e.g. "read book".
     */
    public ToDo(String description) {
        super(description);
    }

    /** Returns the "[T]" type icon followed by the checkbox and description. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
