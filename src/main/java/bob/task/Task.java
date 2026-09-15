package bob.task;

import java.util.Locale;

/**
 * Represents a task and its completion status.
 */
public class Task {
    protected boolean isDone;
    protected final String description;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.isDone = false;
        this.description = description;
    }

    /**
     * Marks the task as done.
     */
    public void markTask() {
        this.isDone = true;
    }

    /**
     * Unmarks the task, the task is undone.
     */
    public void unmarkTask() {
        this.isDone = false;
    }

    /**
     * Returns whether the task is completed or not.
     *
     * @return True if the task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task description.
     *
     * @return Description of this task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task's description contains the specified keyword.
     * Matching is case-insensitive.
     *
     * @param keyword Keyword to search for.
     * @return True if the description contains the keyword.
     */
    public boolean hasKeywordInDescription(String keyword) {
        String normalizedDescription = description.toLowerCase(Locale.ROOT);
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return normalizedDescription.contains(normalizedKeyword);
    }

    /**
     * Returns the task representation to be stored by {@code Storage}.
     *
     * @return Data of the task in the form of a string.
     */
    public String toDataString() {
        return "T | " + (isDone() ? "1" : "0") + " | " + this.description;
    }

    /**
     * Returns a display representation containing the completion status and description.
     *
     * @return Display representation of this task.
     */
    @Override
    public String toString() {
        String completionMark = isDone() ? "X" : " ";
        return "[" + completionMark + "] " + description;
    }
}
