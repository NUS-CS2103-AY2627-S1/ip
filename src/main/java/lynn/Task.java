package lynn;

import java.util.Locale;

/** Represents a task with a description, type, and completion state. */
public class Task {
    /** The user-provided description of this task. */
    protected final String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    private final TaskType taskType;

    /**
     * Creates an incomplete task of the given type.
     *
     * @param description the task description
     * @param taskType the category of the task
     */
    public Task(String description, TaskType taskType) {
        this.description = description;
        this.isDone = false;
        this.taskType = taskType;
    }

    /**
     * Returns the display icon for this task's completion state.
     *
     * @return {@code X} when complete, or a space otherwise
     */
    protected String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    TaskType getTaskType() {
        return taskType;
    }

    boolean isDone() {
        return isDone;
    }

    String getDescription() {
        return description;
    }

    /** Returns whether the description contains the keyword, ignoring case. */
    boolean containsKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /** Returns the task in Lynn's list-display format. */
    @Override
    public String toString() {
        return "[" + taskType.getIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
