package lebronjames.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /** Letter used to identify a to-do in the save file. */
    public static final String FILE_TYPE = "T";

    /**
     * Creates an incomplete to-do task.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toFileFormat() {
        return FILE_TYPE + " | " + getDoneFlag() + " | " + getDescription();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
