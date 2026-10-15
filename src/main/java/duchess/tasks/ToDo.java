package duchess.tasks;

/**
 * Represents a task with no specific deadline nor date.
 */
public class ToDo extends Task {

    /**
     * Creates a task with no specific deadline nor date.
     *
     * @param description The description of the task.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns a string representation of a task with
     * no specific deadline nor date, in user-readable format.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns a string representation of a task with
     * no specific deadline nor date, in storage format.
     */
    @Override
    public String getStorageFormat() {
        return String.format("%s | T |", super.getStorageFormat());
    }
}
