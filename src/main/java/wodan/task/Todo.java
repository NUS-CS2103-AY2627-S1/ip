package wodan.task;

/**
 * A todo task with no date or time attached.
 */
public class Todo extends Task {

    /**
     * Creates a todo with the given description, initially not done.
     *
     * @param description What must be done.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo as {@code [T][status] description}.
     *
     * @return Display text for list replies.
     */
    @Override
    public String toString() {
        return String.format("[T][%s] %s", getStatusIcon(), getDescription());
    }

    /**
     * Returns this todo as {@code T | 0|1 | description | category}.
     *
     * @return One save-file line.
     */
    @Override
    public String toStorageString() {
        return toStoragePrefix(STORAGE_TYPE_TODO) + " | " + getCategory();
    }
}
