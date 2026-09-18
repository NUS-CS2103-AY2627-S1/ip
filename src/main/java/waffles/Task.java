package waffles;

import java.util.Objects;

/**
 * Represents a task in the Waffles task list.
 */
public class Task {
    /** The text describing the task. */
    protected String description;

    /** The completion status of the task. */
    protected TaskStatus status;

    /**
     * Creates a new incomplete task.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = validateDescription(description);
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} when this task is done
     */
    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    /**
     * Updates the task description.
     *
     * @param description the new task description
     */
    public void setDescription(String description) {
        this.description = validateDescription(description);
    }

    /**
     * Returns the status marker used when displaying this task.
     *
     * @return {@code "X"} for a completed task, or a space otherwise
     */
    public String getStatusIcon() {
        return status == TaskStatus.DONE ? "X" : " ";
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns the bracketed status marker used by the current list display.
     *
     * @return {@code "[X]"} for a completed task, or {@code "[ ]"} otherwise
     */
    public String isDoneSymbol() {
        return status == TaskStatus.DONE ? "[X]" : "[ ]";
    }

    /**
     * Returns the task in the format used by the task list.
     *
     * @return the status marker followed by the task description
     */
    @Override
    public String toString() {
        return isDoneSymbol() + " " + description;
    }

    /** Rejects missing task descriptions before they enter the task model. */
    private String validateDescription(String value) {
        String checkedValue = Objects.requireNonNull(value);
        if (checkedValue.isBlank()) {
            throw new IllegalArgumentException("A task description cannot be blank.");
        }
        return checkedValue;
    }
}
