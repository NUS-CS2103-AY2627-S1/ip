package charlie.task;

import charlie.command.UpdateField;
import charlie.exception.CharlieException;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    /** Description supplied by the user. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates a task with its description and completion status.
     *
     * @param description Description of the task.
     * @param isDone Whether the task is completed.
     * @throws CharlieException If the description is empty or cannot be stored safely.
     */
    public Task(String description, boolean isDone) {
        if (description == null || description.isBlank()) {
            throw new CharlieException("Description cannot be empty.");
        }
        if (description.contains("|")) {
            throw new CharlieException("A task description cannot contain |.");
        }
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Returns whether another task has the same type and user-provided details.
     * Completion status is deliberately ignored when checking task uniqueness.
     *
     * @param other Task to compare with this task.
     * @return True when both tasks contain the same identifying details.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equals(other.description);
    }

    /**
     * Marks this task as completed.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markUndone() {
        this.isDone = false;
    }

    /**
     * Creates an independent copy of this task with the requested completion status.
     *
     * @param isDone Completion status for the copy.
     * @return Copied task with the requested status.
     */
    public abstract Task copyWithStatus(boolean isDone);

    /**
     * Returns the shared display representation of a task.
     *
     * @return Completion status and description.
     */
    @Override
    public String toString() {
        String status = (this.isDone) ? "X" : " ";
        return "[" + status + "] " + this.description;
    }

    /**
     * Converts this task to one line in Charlie's save-file format.
     *
     * @return The task data to write to the save file.
     */
    public abstract String saveFileFormat();

    /**
     * Creates a copy of this task with one field replaced.
     *
     * @param updateField Field to replace.
     * @param newValue Replacement value for the field.
     * @return Updated copy of this task.
     * @throws CharlieException If the field is unsupported or the replacement value is invalid.
     */
    public abstract Task createUpdatedTask(UpdateField updateField, String newValue);
}
