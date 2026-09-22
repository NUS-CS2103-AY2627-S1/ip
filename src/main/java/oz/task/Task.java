package oz.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Represents a general task in the task list.
 */
public class Task {
    /** Completion code written for a finished task. */
    public static final String STORAGE_DONE = "1";

    /** Completion code written for an unfinished task. */
    public static final String STORAGE_NOT_DONE = "0";

    /** Description text of the task. */
    protected final String description;

    /** Whether the task has been marked as completed. */
    private boolean isDone;

    /**
     * Constructs a Task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        assert description != null && !description.isBlank()
                : "Task descriptions must be non-null and non-blank";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon indicating whether the task is completed.
     *
     * @return "X" if done, or " " if not done.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Returns whether the task has been marked as completed.
     *
     * @return True if the task is done; false otherwise.
     */
    public boolean isDone() {
        return this.isDone;
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
     * Checks if this task occurs on the given date.
     *
     * @param date The date to check against.
     * @return True if the task occurs on the specified date, false otherwise.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Checks if the task description contains the specified keyword (case-insensitive).
     *
     * @param keyword The keyword to search for in the task description.
     * @return True if the task description contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return false;
        }
        String normalizedDescription = this.description.toLowerCase(Locale.ROOT);
        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        return normalizedDescription.contains(normalizedKeyword);
    }

    /**
     * Converts the task into a file storage representation.
     *
     * @return Formatted string for writing to file.
     */
    public String toFileFormat() {
        return String.format("%s | %s", (this.isDone ? STORAGE_DONE : STORAGE_NOT_DONE), this.description);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatusIcon(), this.description);
    }
}
