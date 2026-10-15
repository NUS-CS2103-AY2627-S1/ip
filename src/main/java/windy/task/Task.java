package windy.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Represents a task with a description and a completion status.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a task with the given description and completion status.
     *
     * @param description the task description.
     * @param isDone whether the task has been completed.
     */
    public Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Returns the symbol used to display the task's completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Checks whether the task has been completed.
     *
     * @return {@code true} if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task description.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Changes the task's completion status.
     *
     * @param isDone the new completion status.
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Returns whether this task's description contains the specified keyword.
     *
     * @param keyword the keyword to search for.
     * @return {@code true} if the task description contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        String normalizedDescription = description.toLowerCase(Locale.ROOT);
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return normalizedDescription.contains(normalizedKeyword);
    }

    /**
     * Converts the task to the format used in the data file.
     *
     * @return the serialized task.
     */
    public abstract String toDataString();

    /**
     * Returns an independent copy of this task.
     *
     * @return a task with the same contents.
     */
    public abstract Task copy();

    /**
     * Checks whether this incomplete task is relevant on the specified date.
     * Tasks without an associated date are not relevant on any date by default.
     *
     * @param date the date to check.
     * @return {@code true} if the task occurs on the date and is not completed.
     */
    public boolean occursOnDate(LocalDate date) {
        return false;
    }
}
