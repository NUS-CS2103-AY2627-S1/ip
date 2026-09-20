package duchess.task;

import java.time.Instant;

/**
 * Represents a task in Duchess's in-memory task list.
 */
public class Task {
    /** The text entered by the user for this task. */
    private String description;

    /** Whether this task has been marked as done. */
    private boolean isDone;

    /** The latest time this task was marked as done, if known. */
    private Instant completedAt;

    /** The category used when displaying this task. */
    private TaskType type;

    /**
     * Creates a new, unfinished task.
     *
     * @param description the text entered by the user
     */
    public Task(String description) {
        this(description, TaskType.TODO);
    }

    /**
     * Creates a new task with a specified category.
     *
     * @param description the text entered by the user
     * @param type the task category
     */
    public Task(String description, TaskType type) {
        assert description != null && !description.isBlank()
                : "A task must have a non-blank description";
        assert type != null : "A task must have a category";
        this.description = description;
        this.isDone = false;
        this.completedAt = null;
        this.type = type;
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
     * Returns this task's category.
     *
     * @return the task category
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns the symbol used to display the task's completion status.
     *
     * @return {@code "X"} for a completed task, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} when the task is complete
     */
    public boolean isDone() {
        return isDone;
    }

    /** Marks this task as done at the current instant. */
    public void markAsDone() {
        markAsDone(Instant.now());
    }

    /**
     * Marks this task as done at a specified instant.
     *
     * <p>A null instant is allowed only when restoring a legacy record that
     * has no completion timestamp.</p>
     *
     * @param completionTime the completion instant, or null when unknown
     */
    public void markAsDone(Instant completionTime) {
        if (!isDone || completedAt == null) {
            completedAt = completionTime;
        }
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
        completedAt = null;
    }

    /**
     * Returns the latest known completion instant.
     *
     * @return the completion instant, or null when it is unknown
     */
    public Instant getCompletedAt() {
        return completedAt;
    }

    /**
     * Returns the common task display, including its completion status.
     *
     * @return the status icon followed by the task description
     */
    @Override
    public String toString() {
        return "[" + type.getIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
