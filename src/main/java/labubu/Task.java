package labubu;

import java.time.format.DateTimeFormatter;

/**
 * Represents a task managed by Labubu.
 */
public abstract class Task {
    protected static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMMM yyyy '['h:mma']'");

    /**
     * Represents a task's progress state.
     */
    public enum Status {
        INCOMPLETE,
        IN_PROGRESS,
        COMPLETED
    }

    protected final String taskTitle;
    private Status status;

    /**
     * Creates a task with the given title and an incomplete status.
     *
     * @param taskTitle Title of the task.
     */
    public Task(String taskTitle) {
        this.taskTitle = taskTitle;
        this.status = Status.INCOMPLETE;
    }

    /**
     * Returns this task's title.
     *
     * @return Task title.
     */
    public String getTaskTitle() {
        return taskTitle;
    }

    /**
     * Updates this task's progress status.
     *
     * @param status New progress status.
     */
    public void setStatus(Status status) {
        assert status != null : "Task status must not be null";
        this.status = status;
    }

    /**
     * Returns this task's progress status.
     *
     * @return Current progress status.
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * Returns the symbol used to display this task's progress state.
     *
     * @return A single-character status indicator.
     */
    public String getStatusIndicator() {
        if (status == Status.COMPLETED) {
            return "X";
        } else if (status == Status.IN_PROGRESS) {
            return "-";
        }
        return " ";
    }

    /**
     * Returns the task description with any subtype-specific details.
     *
     * @return Task description for display in the task list.
     */
    public String getTaskDescription() {
        return taskTitle;
    }

    /**
     * Returns the marker that identifies this task's subtype.
     *
     * @return A task subtype marker.
     */
    public abstract String getMarker();

    /**
     * Returns Task object in save file format.
     *
     * @return A task condensed into single-line String.
     */
    public abstract String toSaveFormat();
}
