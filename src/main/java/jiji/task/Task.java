package jiji.task;

/**
 * Represents a task in the Jiji chatbot.
 * A task contains a textual description and a completion status.
 */
public class Task {

    public static final String ICON_DONE = "X";
    public static final String ICON_NOT_DONE = " ";
    public static final String STORAGE_DONE = "1";
    public static final String STORAGE_NOT_DONE = "0";
    public static final String STORAGE_DELIMITER = " | ";

    /** Description of the task. */
    private final String description;

    /** Completion status of the task (true if done, false otherwise). */
    private boolean isDone;

    /**
     * Constructs a new Task with the given description.
     * Newly created tasks are marked as not done by default.
     *
     * @param description The text description of the task.
     */
    public Task(String description) {
        assert description != null && !description.isBlank() : "Task description cannot be null or blank";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon representing whether the task is completed.
     *
     * @return "X" if the task is done, or " " (single space) if not done.
     */
    public String getStatusIcon() {
        return isDone ? ICON_DONE : ICON_NOT_DONE;
    }

    /**
     * Marks the task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not completed.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the description of the task.
     *
     * @return The task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns true if the task is completed.
     *
     * @return True if completed, false otherwise.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns the string representation of the task formatted for storage persistence.
     *
     * @return Formatted line representation for saving to file.
     */
    public String toFileFormat() {
        return (isDone ? STORAGE_DONE : STORAGE_NOT_DONE) + STORAGE_DELIMITER + description;
    }

    /**
     * Returns the string representation of the task including its status icon.
     *
     * @return Formatted string representation "[status] description".
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Checks if this task is considered a duplicate of another task.
     * Subclasses define specific equality criteria based on their fields.
     *
     * @param other The other task to compare against.
     * @return True if both tasks are considered duplicates, false otherwise.
     */
    public boolean isDuplicateOf(Task other) {
        if (other == null || !this.getClass().equals(other.getClass())) {
            return false;
        }
        return this.description.trim().equalsIgnoreCase(other.description.trim());
    }
}
