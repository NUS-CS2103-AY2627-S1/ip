package topaz.task;

import java.util.Locale;

/**
 * Represents a task entered by the user.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the task description
     */
    public Task(String description) {
        // Parser and Storage validate descriptions before creating tasks. Keeping this
        // invariant here prevents invalid tasks from reaching display and save code.
        assert description != null : "Task descriptions must not be null.";
        assert !description.isBlank() : "Task descriptions must contain text.";
        assert !description.contains("|") : "Task descriptions must not contain the save-file delimiter.";
        this.description = description;
        this.isDone = false;
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
     * Returns the status icon used when displaying this task.
     *
     * @return {@code X} for a completed task, otherwise a blank space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return true if this task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's type.
     *
     * @return the task type
     */
    public abstract TaskType getTaskType();

    /**
     * Returns whether another task has the same type, description, and schedule.
     * Completion status, description case, and repeated spaces do not change identity.
     *
     * @param other the task to compare
     * @return true if adding the other task would duplicate this task
     */
    public boolean hasSameDetails(Task other) {
        return other != null && getTaskType() == other.getTaskType()
                && normalizedDescription().equals(other.normalizedDescription()) && hasSameSchedule(other);
    }

    /**
     * Normalizes only the underlying description, excluding display decorations.
     */
    private String normalizedDescription() {
        return description.replaceAll("\\h+", " ").strip().toLowerCase(Locale.ROOT);
    }

    /**
     * Compares scheduling details after the caller has checked that task types match.
     */
    protected boolean hasSameSchedule(Task other) {
        return true;
    }

    /**
     * Returns this task in the format used by the save file.
     *
     * @return a line representing this task
     */
    public abstract String toFileString();

    /**
     * Returns the task type and completion status for display.
     *
     * @return a display icon such as {@code [D][ ]}
     */
    public String getDisplayIcon() {
        return "[" + getTaskType().getIcon() + "][" + getStatusIcon() + "]";
    }
}
