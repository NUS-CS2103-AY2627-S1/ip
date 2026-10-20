package yapper;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    /** Text describing the task. */
    protected String description;
    /** Whether the task has been marked completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon used to display the task's completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Checks for a case-insensitive substring in the task description.
     *
     * @param keyword Nonempty search text validated by the parser.
     * @return Whether the description contains the search text.
     */
    public boolean containsKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task in the format written to persistent storage.
     *
     * @return Pipe-separated task type, completion state, and description.
     */
    public String toDataString() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns whether this task occurs on the given date.
     * Tasks without dates do not occur on any particular date.
     *
     * @param date Date to check.
     * @return {@code true} if the task occurs on that date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the task in the format used by the chatbot.
     *
     * @return Task status followed by its description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
