package doe;

import java.time.LocalDateTime;

/**
 * Represents a Todo item and whether it has been completed.
 */
public class Task {
    protected String taskName;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given name.
     *
     * @param taskName Description of the task.
     */
    public Task(String taskName) {
        assert taskName != null : "A task must have a description";
        this.taskName = validateDescription(taskName);
        this.isDone = false;
    }

    /** Validates and trims a description before a task or multi-step operation accepts it. */
    public static String validateDescription(String taskName) {
        if (taskName == null || taskName.isBlank()) {
            throw new IllegalArgumentException("task description cannot be blank.");
        }
        if (taskName.contains("|") || taskName.contains("\n") || taskName.contains("\r")) {
            throw new IllegalArgumentException("task description cannot contain '|', or line breaks.");
        }
        return taskName.strip();
    }

    /**
     * Returns the date used for chronological sorting, or null for an undated task.
     *
     * @return The task's scheduled date and time, or null if it has none.
     */
    public LocalDateTime getDate() {
        return null;
    }

    /**
     * Returns the task description.
     *
     * @return The name or description of the task.
     */
    public String getDescription() {
        return taskName;
    }

    /**
     * Returns the status icon displayed beside this task.
     *
     * @return An "x" if the task is done, or a blank space if incomplete.
     */
    public String getStatusIcon() {
        return isDone ? "x" : " ";
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
     * Creates String for file storage.
     *
     * @return File-formatted string detailing task completion status and description.
     */
    public String toFileFormat() {
        return isDone + "|" + taskName;
    }

    /**
     * Returns this task in the format used when displaying a task list.
     *
     * @return Formatted UI string indicating task status and description.
     * */
    @Override
    public String toString() {
        return "[][" + getStatusIcon() + "] " + taskName;
    }
}
