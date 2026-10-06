package caesar.task;

/**
 * A task without a deadline or scheduled time.
 */
public class ToDo extends Task {
    /** Creates a basic task with the supplied description. */
    public ToDo(String description) {
        super(description);
    }

    /** Returns the task in the task-list display format for basic tasks. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
