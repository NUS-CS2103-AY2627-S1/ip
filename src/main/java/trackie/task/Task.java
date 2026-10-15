package trackie.task;

import java.util.Locale;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    private final String description;
    private final TaskType type;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     * @param type type of the task
     */
    protected Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns an icon representing the completion status of this task.
     *
     * @return {@code X} if completed, or a space if incomplete
     */
    public String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns a line representing this task in Trackie's data-file format.
     *
     * @return serialized task type, status, and description
     */
    public String toDataString() {
        String doneValue = this.isDone ? "1" : "0";
        return this.type.getIcon() + " | " + doneValue + " | " + this.description;
    }

    /**
     * Checks whether this task's description contains a keyword, ignoring case.
     *
     * @param keyword text to search for
     * @return true if the description contains the keyword
     */
    public boolean containsKeyword(String keyword) {
        String normalizedDescription = this.description.toLowerCase(Locale.ROOT);
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return normalizedDescription.contains(normalizedKeyword);
    }

    /**
     * Returns the task in a form suitable for display to the user.
     *
     * @return formatted completion status and description
     */
    @Override
    public String toString() {
        return "[" + this.type.getIcon() + "][" + getStatusIcon() + "] " + this.description;
    }
}
