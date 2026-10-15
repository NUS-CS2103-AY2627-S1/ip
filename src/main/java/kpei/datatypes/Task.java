package kpei.datatypes;

/**
 * Represents a standard task in the task list.
 * Serves as the base class for specialized task types such as {@link Deadline} and {@link Event}.
 */
public class Task {
    protected String type;
    protected boolean isMarked;
    protected String description;

    /**
     * Constructs a {@code Task} task with a specified completion status and description.
     * Also used as a base constructor by subclasses (e.g., {@link Deadline}, {@link Event}).
     *
     * @param isMarked Whether this task is marked as completed.
     * @param description The description of the task.
     */
    public Task(boolean isMarked, String description) {
        this.type = "todo";
        this.isMarked = isMarked;
        this.description = description;
    }

    /**
     * Constructs an unmarked {@code Task} task with default completion status set to {@code false}.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        this(false, description);
    }

    /**
     * Returns the description of this task.
     *
     * @return The description of the task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the type of this task.
     *
     * @return The task type string.
     */
    public String getType() {
        return type;
    }

    /**
     * Checks if this task is marked as completed.
     *
     * @return {@code true} if marked, {@code false} otherwise.
     */
    public boolean isMarked() {
        return isMarked;
    }

    /**
     * Marks this task as completed.
     */
    public void mark() {
        this.isMarked = true;
    }

    /**
     * Unmarks this task, setting its status to not completed.
     */
    public void unmark() {
        this.isMarked = false;
    }

    @Override
    public String toString() {
        return String.format("[%s][%s] %s", type, isMarked ? "X" : " ", description);
    }

    /**
     * Formats this task into a delimited string representation suitable for storage.
     *
     * @return Formatted string representing the task for file storage.
     */
    public String toFileFormat() {
        return String.format("%s | %s | %s", type, isMarked, description);
    }
}
