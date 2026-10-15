package esther.task;

import java.util.Locale;

/**
 * Represents a task tracked by Esther.
 */
public class Task {
    /** Description of this task. */
    protected String description;

    /** Whether this task is completed. */
    protected boolean isDone;

    /** Type of this task. */
    protected final TaskType type;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     * @param type Type of the task.
     */
    public Task(String description, TaskType type) {
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
     * Returns whether this task description contains the specified keyword.
     *
     * @param keyword Keyword to search for.
     * @return Whether the description contains the keyword.
     */
    public boolean hasKeyword(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return description.toLowerCase(Locale.ROOT).contains(normalizedKeyword);
    }

    /**
     * Returns whether this task is completed.
     *
     * @return Whether this task is completed.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns the status icon for this task.
     *
     * @return Status icon.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Returns this task in the format used by the data file.
     *
     * @return Serialized representation of this task.
     */
    public String toDataString() {
        return String.format("%s | %d | %s",
                type.getSymbol(), isDone ? 1 : 0, escapeDataField(description));
    }

    /**
     * Escapes characters that have a special meaning in the data format.
     *
     * @param value Field value to escape.
     * @return Escaped field value.
     */
    protected static String escapeDataField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    @Override
    public String toString() {
        return String.format(
                "[%s][%s] %s",
                this.type.getSymbol(),
                getStatusIcon(),
                this.description
        );
    }
}
