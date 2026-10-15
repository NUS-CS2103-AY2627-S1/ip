package bos;

/**
 * Represents a task without a deadline or scheduled time.
 */
public class TodoTask extends Task {
    static final String STORAGE_TYPE = "T";

    /**
     * Creates a todo task with the given description.
     *
     * @param description task description.
     */
    public TodoTask(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String formatForStorage() {
        return formatStorageFields(STORAGE_TYPE);
    }
}
