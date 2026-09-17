package meow;

/**
 * Represents a todo task.
 */
public class Todo extends Task {
    static final String STORAGE_TYPE = "T";

    /**
     * Creates a todo task with the specified description.
     *
     * @param description the description of the task
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T][" + super.getStatusForDisplay() + "] " + super.getDescription();
    }

    @Override
    public String toFileString() {
        return STORAGE_TYPE + " | " + super.getStatusForFile() + " | " + super.getDescription();
    }
}
