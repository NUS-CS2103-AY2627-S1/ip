package daddy.task;

import java.time.LocalDate;

/**
 * Represents one task in the task list.
 */
public class Task {
    /** Stores the text that describes this task. */
    private final String description;
    /** Stores the category used to display and persist this task. */
    private final TaskType type;
    /** Records whether the task has been completed. */
    private boolean isDone;

    /**
     * Creates a task that is initially not done.
     *
     * @param description the task description
     */
    public Task(String description) {
        this(description, TaskType.GENERAL);
    }

    /**
     * Creates a task with the specified type.
     *
     * @param description the task description
     * @param type the kind of task being created
     */
    protected Task(String description, TaskType type) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("task description must not be blank");
        }
        if (description.contains("|")) {
            throw new IllegalArgumentException("task description must not contain the storage separator");
        }
        if (type == null) {
            throw new IllegalArgumentException("task type must not be null");
        }
        this.description = description;
        this.type = type;
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
     * Returns the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the description used when displaying this task.
     *
     * @return the task description without a type-specific date or time
     */
    public String getDisplayDescription() {
        return description;
    }

    /**
     * Returns the icon used to display this task's completion state.
     *
     * @return {@code X} for a done task, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return whether the task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the marker used to identify this task type.
     *
     * @return an empty marker for a general task
     */
    public String getTypeIcon() {
        return type.getIcon();
    }

    /**
     * Returns this task's type for persistence.
     *
     * @return the task type
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns whether another task has the same type and user-entered details.
     *
     * <p>Completion state is excluded because marking a task does not make it a different task.</p>
     *
     * @param other the task to compare
     * @return whether both tasks represent the same work
     */
    public boolean hasSameDetailsAs(Task other) {
        return other != null
                && type == other.type
                && description.equalsIgnoreCase(other.description);
    }

    /**
     * Returns whether this task occurs on the supplied date.
     *
     * @param date the date to check
     * @return {@code false}, because a general task has no associated date
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }
}
