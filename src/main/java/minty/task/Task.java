package minty.task;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
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

    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the date used for reminders, if this task has one.
     *
     * @return empty for undated tasks.
     */
    public Optional<LocalDate> getReminderDate() {
        return Optional.empty();
    }

    /**
     * Checks whether this task occurs on a given date.
     *
     * <p>Tasks without dates do not occur on any particular date.
     *
     * @param date date to check.
     * @return {@code true} if the task occurs on the date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Checks whether this task's description contains a keyword.
     *
     * @param keyword keyword to search for.
     * @return {@code true} if the description contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Returns the task in the format used by the save file.
     *
     * @return serialized task.
     */
    public String toDataString() {
        return "T | " + (isDone ? "1" : "0") + " | " + escapeDataField(description);
    }

    /**
     * Compares task type, description, and dates without considering completion.
     *
     * @param other task to compare.
     * @return whether both tasks describe the same work.
     */
    public boolean hasSameDetails(Task other) {
        String details = toDataString().replaceFirst(" \\| [01] \\| ", " | ");
        String otherDetails = other.toDataString().replaceFirst(" \\| [01] \\| ", " | ");
        return details.equals(otherDetails);
    }

    /**
     * Escapes characters that have special meaning in the save-file format.
     *
     * @param value task field to escape.
     * @return escaped field.
     */
    protected static String escapeDataField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Returns the task in the format used by Minty's responses.
     *
     * @return status icon followed by the task description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
