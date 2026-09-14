package arrodes.task;

/**
 * Represents one task remembered by Arrodes.
 */
public class Task {
    /** Text describing what needs to be done. */
    private final String description;

    /** Whether the task has been completed. */
    private boolean isDone;
    /** Tag associated with the task. */
    private String tag = "";

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task
     * @throws IllegalArgumentException if the description is null or blank
     */
    public Task(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be null or blank.");
        }
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task's description.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the status symbol displayed in task lists.
     *
     * @return {@code X} for a completed task, or a blank space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if the task is complete
     */
    public boolean isDone() {
        return isDone;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }
    /**
     * Associates the given tag with this task.
     *
     * @param tag tag to associate with the task
     * @throws IllegalArgumentException if the tag is null
     */
    public void tagWith(String tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Task tag cannot be null.");
        }
        this.tag = tag;
    }

    /**
     * Returns whether this task has the same type and task-specific details as another task.
     *
     * @param other task to compare with this task
     * @return {@code true} if both tasks represent the same request
     */
    public boolean hasSameDetailsAs(Task other) {
        if (other == null || getClass() != other.getClass()
                || !description.equals(other.description)) {
            return false;
        }
        if (this instanceof Deadline) {
            return hasSameDeadlineDetails(other);
        }
        if (this instanceof Event) {
            return hasSameEventDetails(other);
        }
        return true;
    }

    /** Returns whether two deadline tasks have the same due date. */
    private boolean hasSameDeadlineDetails(Task other) {
        Deadline deadline = (Deadline) this;
        Deadline otherDeadline = (Deadline) other;
        return deadline.getDueBy().equals(otherDeadline.getDueBy());
    }

    /** Returns whether two event tasks have the same interval. */
    private boolean hasSameEventDetails(Task other) {
        Event event = (Event) this;
        Event otherEvent = (Event) other;
        return event.getStartAt().equals(otherEvent.getStartAt())
                && event.getEndAt().equals(otherEvent.getEndAt());
    }

    /**
     * Returns the tag associated with this task.
     *
     * @return task tag, or an empty string if the task is not tagged
     */
    public String getTag() {
        return tag;
    }

    /**
     * Returns a formatted display representation of this task.
     *
     * @return task status and description, followed by the tag prefixed with {@code #} when present
     */
    @Override
    public String toString() {
        boolean hasTag = !tag.isBlank();
        if (hasTag) {
            return String.format("[%s] %s #%s", getStatusIcon(), getDescription(), tag);
        }
        return String.format("[%s] %s", getStatusIcon(), getDescription());
    }
}
