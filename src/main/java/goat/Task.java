package goat;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents a task and whether it has been completed.
 */
public abstract class Task {
    /** Human-readable task description. */
    protected String description;
    /** Whether the task is completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        assert description != null : "Task description should not be null";
        assert !description.isBlank() : "Task description should not be blank";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon that represents the task's completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
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
     * Returns the date used when chronologically sorting tasks.
     * Undated tasks return an empty value and are placed after dated tasks.
     *
     * @return scheduling date, if this task has one
     */
    public Optional<LocalDate> getSchedulingDate() {
        return Optional.empty();
    }

    /**
     * Returns the numeric completion value used in the data file.
     *
     * @return {@code 1} if the task is done, or {@code 0} otherwise
     */
    protected int getFileStatus() {
        return isDone ? 1 : 0;
    }

    /**
     * Formats this task's common data together with any type-specific fields.
     * Backslashes and pipe characters are escaped so saved text can be read unambiguously.
     *
     * @param taskType one-letter task type
     * @param additionalFields fields specific to the task subtype
     * @return serialized task data
     */
    protected String formatFileData(String taskType, String... additionalFields) {
        StringBuilder result = new StringBuilder(taskType)
                .append(" | ")
                .append(getFileStatus())
                .append(" | ")
                .append(escapeFileField(description));
        for (String field : additionalFields) {
            result.append(" | ").append(escapeFileField(field));
        }
        return result.toString();
    }

    /**
     * Escapes characters that have special meaning in the data-file format.
     *
     * @param field text to escape
     * @return escaped text
     */
    private String escapeFileField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Returns a representation of this task suitable for saving to disk.
     *
     * @return serialized task data
     */
    public abstract String toFileString();

    /**
     * Returns the task in the format used when displaying task lists.
     *
     * @return the status icon followed by the task description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
