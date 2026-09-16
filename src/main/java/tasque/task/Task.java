package tasque.task;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Represents a task with a description, type, and completion state.
 */
public class Task {
    // The suffix distinguishes Base64 descriptions from legacy plain-text records.
    private static final String ENCODED_DESCRIPTION_SUFFIX = "2";
    private static final String COMPLETED_MARKER = "1";
    private static final String INCOMPLETE_MARKER = "0";

    protected String description;
    protected boolean isDone;
    protected TaskType type;

    /**
     * Creates an incomplete task with the specified description and type.
     *
     * @param description Description of the task.
     * @param type Type of the task.
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns {@code X} for a completed task and a space otherwise.
     *
     * @return {@code X} if completed, or a space otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns the task description.
     *
     * @return Description supplied when this task was created.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return {@code true} if this task is completed.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Compares task details without considering completion state.
     * Subclasses include their additional details in this comparison.
     *
     * @param other Task to compare with this task.
     * @return Whether both tasks have the same type and details.
     */
    public boolean hasSameDetails(Task other) {
        return other != null && getClass() == other.getClass()
                && this.type == other.type && this.description.equals(other.description);
    }

    /**
     * Returns this task in its storage-file representation.
     *
     * @return Storage representation of this task.
     */
    public String toStorageString() {
        String encodedDescription = Base64.getEncoder().encodeToString(
                this.description.getBytes(StandardCharsets.UTF_8));
        return this.type.getSymbol() + ENCODED_DESCRIPTION_SUFFIX + " | "
                + (this.isDone ? COMPLETED_MARKER : INCOMPLETE_MARKER) + " | "
                + encodedDescription;
    }

    /**
     * Returns the task in the format displayed by the console UI.
     *
     * @return Task display text including its type, status, and description.
     */
    @Override
    public String toString() {
        return "[" + this.type.getSymbol() + "][" + this.getStatusIcon() + "] "
                + this.getDescription();
    }
}
