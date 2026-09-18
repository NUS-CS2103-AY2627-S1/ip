package luna.task;

/**
 * Represents a generic task tracked by Luna.
 */
public class Task {
    private static final int MAX_DESCRIPTION_LENGTH = 500;

    private final String description;
    private boolean isDone;

    /**
     * Creates a task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A task description must not be blank.");
        }
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("A task description must be 500 characters or fewer.");
        }
        if (description.chars().anyMatch(character -> Character.isISOControl(character))) {
            throw new IllegalArgumentException("A task description must not contain control characters.");
        }

        this.description = description.trim();
        this.isDone = false;
    }

    /**
     * Returns the status icon of the task.
     *
     * @return Status icon showing whether the task is done.
     */
    public String getStatusIcon() {
        return isDone ? "[X]" : "[ ]";
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
        assert isDone : "A task marked as done must report that it is done";
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
        assert !isDone : "A task marked as not done must report that it is not done";
    }

    /**
     * Returns the description of this task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is done.
     *
     * @return {@code true} if the task is done, {@code false} otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether another task has the same type and user-entered details.
     * Completion status is deliberately ignored when detecting duplicates.
     *
     * @param other Task to compare with this task.
     * @return {@code true} if both tasks represent the same task details.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && normalizeDescription(description).equalsIgnoreCase(normalizeDescription(other.description));
    }

    /**
     * Normalizes insignificant spacing before task descriptions are compared.
     */
    private static String normalizeDescription(String value) {
        return value.replaceAll("\\s+", " ");
    }

    /**
     * Returns the text format used to save this task to disk.
     *
     * @return Serialized representation of this task.
     */
    public String toStorageString() {
        return "T | " + getStorageStatus() + " | " + description;
    }

    /**
     * Returns the numeric status used in the save file.
     *
     * @return {@code 1} if done, otherwise {@code 0}.
     */
    protected String getStorageStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns the user-facing text representation of this task.
     *
     * @return Status icon followed by the task description.
     */
    @Override
    public String toString() {
        return getStatusIcon() + " " + description;
    }
}
