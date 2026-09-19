package eli;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents a task that can be marked as done or not done.
 * Specific task types provide their own type icon and extra details.
 */
public abstract class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String description;
    private final TaskType taskType;
    private boolean isDone;

    /**
     * Creates a task that is initially not done.
     *
     * @param description the task description
     * @param taskType the type of this task
     */
    protected Task(String description, TaskType taskType) {
        assert description != null : "Task description must not be null";
        assert !description.isBlank() : "Task description must not be blank";
        assert taskType != null : "Task type must not be null";
        this.description = description;
        this.taskType = taskType;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not completed. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns the description used to search for this task. */
    public String getDescription() {
        return description;
    }

    /** Returns this task's type for grouping and sorting. */
    TaskType getTaskType() {
        return taskType;
    }

    /** Returns the task's chronological sort value, if it has a supported date. */
    Optional<LocalDateTime> getSortDate() {
        return Optional.empty();
    }

    /** Returns whether another task has the same type and description. */
    boolean hasSameDetails(Task other) {
        return other != null
                && taskType == other.taskType
                && description.equalsIgnoreCase(other.description);
    }

    /**
     * Returns the icon that shows whether this task is done.
     *
     * @return {@code X} for a done task, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    @Override
    public String toString() {
        return "[" + taskType.getIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
