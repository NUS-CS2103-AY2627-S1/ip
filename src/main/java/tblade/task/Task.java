package tblade.task;

import java.util.Objects;

/**
 * Represents one task in the task list and whether it has been completed.
 */
public abstract class Task {
    private final String description;
    private final TaskType type;
    private boolean isDone;

    /**
     * Creates an unfinished task with the given description.
     *
     * @param description text that describes the task
     * @param type the category of this task
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Returns the completion icon used when displaying this task.
     *
     * @return {@code X} for a completed task, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the text that describes this task.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns this task's category for storage.
     *
     * @return the category of this task
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if this task is completed
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns any date or time details belonging to this task.
     *
     * @return formatted date or time details, or an empty string for a todo
     */
    protected abstract String getTimeDetails();

    /**
     * Returns this task in the format used by the task list.
     *
     * @return formatted task text
     */
    @Override
    public String toString() {
        return "[" + type.getIcon() + "][" + getStatusIcon() + "] " + description + getTimeDetails();
    }

    /**
     * Returns whether this task and the given object have the same type and description.
     * Completion status is not part of equality, so marking a task done does not change what
     * it is considered a duplicate of. Subclasses with their own fields (e.g. a deadline's date)
     * extend this with their own comparison.
     *
     * @param other the object to compare against
     * @return {@code true} if {@code other} is a task of the same type with the same description
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Task)) {
            return false;
        }
        Task task = (Task) other;
        return type == task.type && description.equals(task.description);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return the hash code for this task's type and description
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, description);
    }
}
