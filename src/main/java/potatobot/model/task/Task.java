package potatobot.model.task;

import java.util.Locale;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private final String description;
    private boolean isMarked = false;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        this.description = description;
    }

    /**
     * Marks this task as completed.
     */
    public void markDone() {
        isMarked = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markReset() {
        isMarked = false;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code "X"} when completed, or a space when incomplete.
     */
    public String getStatusIcon() {
        return isMarked ? "X" : " ";
    }

    /**
     * Returns whether this task's description contains the specified keyword.
     * The comparison is case-insensitive.
     *
     * @param keyword Keyword to find in the description.
     * @return {@code true} if the description contains the keyword.
     */
    public boolean matchesKeyword(String keyword) {
        String normalizedDescription = description.toLowerCase(Locale.ROOT);
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return normalizedDescription.contains(normalizedKeyword);
    }

    /**
     * Returns this task's description.
     *
     * @return Task description.
     */
    @Override
    public String toString() {
        return description;
    }
}
