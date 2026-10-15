package es;

/**
 * Represents a task with no associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates a to-do task.
     *
     * @param description the text describing the task
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

    /**
     * Returns the encoded to-do line for saving to disk.
     *
     * @return a line in the form {@code T | 0 | description}
     */
    @Override
    public String toStorageLine() {
        return toStoragePrefix();
    }
}
