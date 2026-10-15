package cooper.task;

/**
 * Represents a task without an associated date or time.
 */
public class ToDo extends Task {

    /** Creates an incomplete todo with the specified description. */
    public ToDo(String description) {
        super(description);
    }

    /** Creates a todo with the specified description and completion status. */
    public ToDo(String description, boolean isDone) {
        super(description, isDone);
    }

    /** Returns the display form of this todo. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /** Returns the storage representation of this todo. */
    @Override
    public String toDataString() {
        return String.format("%s | %s", "T", super.toDataString());
    }
}
