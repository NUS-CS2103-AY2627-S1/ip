package penguin.task;

import java.time.LocalDate;
import java.util.Locale;

import penguin.enums.TaskType;

/**
 * Represents a task that can be marked as completed or incomplete.
 */
public class Task {
    /** Delimiter reserved by the persistent storage format. */
    public static final String STORAGE_DELIMITER = "|";

    /** The description of this task. */
    private final String description;

    /** Whether this task has been completed. */
    private boolean isDone;

    /** Type of this task. */
    private final TaskType type;

    /**
     * Creates an incomplete task of the specified type.
     *
     * @param taskDescription the description of the task.
     * @param taskType the type of the task.
     */
    Task(String taskDescription, TaskType taskType) {
        this.description = taskDescription;
        this.isDone = false;
        this.type = taskType;
    }

    /**
     * Returns the display marker for this task's completion status.
     *
     * @return {@code "X"} if the task is done, or a space otherwise.
     */
    public String getStatus() {
        return isDone ? "X" : " ";
    }

    /**
     * Checks whether this task has been completed.
     *
     * @return true if the task is completed, otherwise false.
     */
    protected boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as completed.
     */
    public void markDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markUndone() {
        isDone = false;
    }

    /**
     * Creates an independent copy of this task.
     *
     * @return copy with the same completion status.
     */
    public Task copy() {
        Task copy = new Task(description, type);
        copyCompletionStatusTo(copy);
        return copy;
    }

    /**
     * Copies this task's completion status to another task.
     *
     * @param copy task that should receive this task's completion status.
     */
    protected void copyCompletionStatusTo(Task copy) {
        assert copy != null : "Task copy must not be null";

        if (isDone) {
            copy.markDone();
        }
    }

    /**
     * Returns this task's description for subclass copy operations.
     *
     * @return this task's description.
     */
    protected String getDescription() {
        return description;
    }

    /**
     * Returns this task in the format used for persistent storage.
     *
     * @return task type, completion status, and description separated by pipes.
     */
    public String toStorageFormat() {
        String status = isDone ? "1" : "0";
        return String.format("%s | %s | %s", type.getSymbol(), status, description);
    }

    /**
     * Checks whether this task occurs on the specified date.
     *
     * @param date date to check.
     * @return false because a normal to-do has no date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Checks whether this task description contains the specified keyword,
     * ignoring capitalization.
     *
     * @param keyword keyword or phrase to search for.
     * @return true if the description contains the keyword, ignoring capitalization.
     */
    public boolean containsKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        String lowerDescription = description.toLowerCase(Locale.ROOT);
        return lowerDescription.contains(lowerKeyword);
    }

    /**
     * Returns the task status, type, and description formatted for display.
     *
     * @return the task status, type, and description formatted for display.
     */
    @Override
    public String toString() {
        return "[" + type.getSymbol() + "][" + getStatus() + "] " + description;
    }
}
