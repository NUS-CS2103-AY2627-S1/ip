package nova.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Represents a task and whether it has been completed.
 */
public abstract class Task {
    /** Description shown for this task. */
    private final String description;

    /** Whether this task has been completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        assert description != null : "Task description must not be null";
        assert !description.isBlank() : "Task description must not be blank";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the symbol used to display the task's completion state.
     *
     * @return {@code X} when done, or a space when not done.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's description.
     *
     * @return task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the letter used to identify this task's type.
     *
     * @return the task type icon.
     */
    protected abstract String getTypeIcon();

    /**
     * Returns whether this task is scheduled on the given date.
     * Todos have no date, so subclasses with dates override this method.
     *
     * @param date date being searched.
     * @return {@code true} if the task occurs on that date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether this task's description contains a keyword, ignoring letter case.
     *
     * @param keyword keyword to find in the description.
     * @return {@code true} if the description contains the keyword.
     */
    public boolean hasDescriptionContaining(String keyword) {
        String normalizedDescription = description.toLowerCase(Locale.ROOT);
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return normalizedDescription.contains(normalizedKeyword);
    }

    /**
     * Formats this task for storage in Nova's data file.
     *
     * @return the task type, completion state, and description.
     */
    public String toFileString() {
        return getTypeIcon() + " | " + (isDone ? "1" : "0") + " | "
                + escapeFileField(description);
    }

    /**
     * Escapes storage separator characters so task text can be loaded exactly.
     *
     * @param value task text to store.
     * @return escaped text safe for the pipe-separated file format.
     */
    protected String escapeFileField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Formats this task with its completion status and description.
     *
     * @return the display form of this task.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
