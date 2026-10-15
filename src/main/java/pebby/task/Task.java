package pebby.task;

/**
 * Represents the shared completion state and description of a task.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the supplied description.
     */
    public Task(String description) {
        validateDescription(description);
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as complete.
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
     * Restores this task's completion state from saved data.
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Returns the text that describes this task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the completion-status icon for this task.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns whether this task has been completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /** Returns whether this task has the same user-visible details as another task. */
    public boolean hasSameDetails(Task other) {
        return other != null && getClass().equals(other.getClass())
                && description.equals(other.description);
    }

    /** Rejects descriptions that cannot be safely represented as one task. */
    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Please provide a description.");
        }
        if (!description.equals(description.trim())) {
            throw new IllegalArgumentException("Task descriptions cannot start or end with spaces.");
        }
        if (description.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Task descriptions cannot contain control characters.");
        }
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
