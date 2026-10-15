package nova.task;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Represents the common description and completion state shared by all tasks.
 */
public class Task {
    /**
     * Description displayed to the user and stored in the data file.
     */
    protected String description;

    /**
     * Completion state of this task.
     */
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
     * Returns the icon used to show the task's completion status.
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
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return {@code true} if this task is completed
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's completion state in the data-file format.
     *
     * @return {@code 1} if this task is done, or {@code 0} otherwise
     */
    protected String getDataStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns this task in the format used by the data file.
     *
     * @return serialized task.
     */
    public String toDataString() {
        return "V2 | T | " + getDataStatus() + " | " + encodeDataField(description);
    }

    /**
     * Returns a text field encoded safely for storage in a delimited record.
     *
     * @param value text field to encode
     * @return Base64-encoded text field
     */
    protected String encodeDataField(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Returns the task in its display format, including its status icon.
     *
     * @return formatted task.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
