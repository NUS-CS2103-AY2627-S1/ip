package sumo.task;

/**
 * Represents a task entered by the user.
 */
public class Task {
    private static final String INCOMPLETE_STATUS = "0";
    private static final String COMPLETE_STATUS = "1";
    private static final String STORAGE_FIELD_SEPARATOR = " | ";

    /** Description supplied by the user. */
    private final String description;
    /** Indicates whether the task has been completed. */
    private boolean isDone;

    /**
     * Creates a new incomplete task with the given description.
     *
     * @param description the task text.
     * @throws IllegalArgumentException if the description cannot be stored safely.
     */
    public Task(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Task descriptions cannot be blank.");
        }
        if (description.codePoints().anyMatch(character -> (Character.isISOControl(character) && character != '\t')
                || character == '\u2028' || character == '\u2029')) {
            throw new IllegalArgumentException("Task descriptions cannot contain line breaks or control characters.");
        }
        String normalizedDescription = description.replaceAll("\\h+", " ").strip();
        if (normalizedDescription.isEmpty() || normalizedDescription.contains("|")) {
            throw new IllegalArgumentException("Task descriptions must be non-blank and cannot contain '|'.");
        }
        this.description = normalizedDescription;
        this.isDone = false;
    }

    /**
     * Returns the icon used to show whether this task is done.
     *
     * @return "X" for a completed task or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the icon used to show the type of this task.
     *
     * <p>The base task keeps the original display format for backwards
     * compatibility. Concrete task types override this method when they have
     * a type icon.</p>
     *
     * @return an empty string for a general task.
     */
    public String getTypeIcon() {
        return "";
    }

    /**
     * Returns this task in the line-based format used for persistent storage.
     *
     * @return the task type, completion status, and description.
     */
    public String toDataString() {
        String completionStatus = isDone ? COMPLETE_STATUS : INCOMPLETE_STATUS;
        return getTypeIcon() + STORAGE_FIELD_SEPARATOR + completionStatus
                + STORAGE_FIELD_SEPARATOR + getDescription();
    }

    /**
     * Returns this task in the original Sumo display format.
     *
     * @return the formatted task.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + getDescription();
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return whether this task has been completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task description.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether another task has the same type and description, ignoring case and completion status.
     * Dated task types additionally compare their dates and times.
     *
     * @param other task to compare.
     * @return whether the tasks describe the same work.
     */
    public boolean hasSameDetails(Task other) {
        return other != null && getClass().equals(other.getClass())
                && description.equalsIgnoreCase(other.description);
    }
}
