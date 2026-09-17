package dog.model;

/**
 * Abstract base class representing a task in the Dog application.
 * Provides common functionality for all task types.
 */
public abstract class Task {
    private static final String INCOMPLETE_STATUS_ICON = " ";
    private static final String COMPLETE_STATUS_ICON = "X";

    protected String description;
    protected boolean isDone;

    /**
     * Creates a task with the specified description.
     *
     * @param description the task description.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Creates a task with the specified description and completion status.
     *
     * @param description the task description.
     * @param isDone      true if the task is completed, false otherwise.
     */
    public Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Checks if the given string is an invalid status icon.
     *
     * @param statusIcon the status icon.
     * @return true if the status icon is invalid, false otherwise.
     */
    protected static boolean isInvalidStatusIcon(String statusIcon) {
        return !statusIcon.equals(COMPLETE_STATUS_ICON) && !statusIcon.equals(INCOMPLETE_STATUS_ICON);
    }

    /**
     * Checks if the given status icon represents a completed task.
     * Method assumes the status is valid, as per <code>isInvalidStatusIcon</code>, otherwise behavior is undefined.
     *
     * @param statusIcon the valid status icon.
     * @return true if the status icon is valid, false otherwise.
     */
    protected static boolean isCompletedStatusIcon(String statusIcon) {
        return statusIcon.equals(COMPLETE_STATUS_ICON);
    }

    /**
     * Creates a Task from its save format string, or returns null if invalid.
     * Implemented by each subclass to handle its own parsing logic.
     *
     * @param saveString the full save-format string (e.g., "T | X | description")
     * @return the parsed Task, or null if the type or format is invalid
     */
    public static Task fromSaveFormat(String saveString) {
        // Task base class checks the type prefix and delegates
        String[] parts = saveString.split(" \\| ", 2);
        if (parts.length < 1) {
            return null;
        }
        String type = parts[0].trim();
        switch (type) {
            case Todo.TASK_ICON:
                return Todo.fromSaveFormat(saveString);
            case Deadline.TASK_ICON:
                return Deadline.fromSaveFormat(saveString);
            case Event.TASK_ICON:
                return Event.fromSaveFormat(saveString);
            default:
                return null;
        }
    }

    public boolean getStatus() {
        return this.isDone;
    }

    /**
     * Returns a string representation of the task for saving to file.
     *
     * @return the task in save format.
     */
    public abstract String toSaveFormat();

    /**
     * Returns the status icon for the task.
     *
     * @return "X" if completed, " " if not completed.
     */
    public String getStatusIcon() {
        return (getStatus() ? COMPLETE_STATUS_ICON : INCOMPLETE_STATUS_ICON);
    }

    /**
     * Marks the task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Checks if the task description contains the given keyword.
     *
     * @param keyword the keyword to search for (case-insensitive).
     * @return true if the description contains the keyword, false otherwise.
     */
    public boolean containsKeyword(String keyword) {
        return this.description.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Returns a string representation of the task for display.
     *
     * @return the task in display format.
     */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }
}
