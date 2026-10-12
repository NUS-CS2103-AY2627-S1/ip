package echo.task;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents a task in E.C.H.O.'s task list.
 */
public class Task {
    /** Text entered by the user to describe the task. */
    private final String description;

    /** Whether the task has been completed. */
    private boolean isDone;

    /**
     * Creates a new task that is initially not done.
     *
     * @param description task description.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} when done; otherwise, a blank space.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if completed; false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Checks whether another task has duplicate details to this task.
     *
     * @param other task to compare against.
     * @return true if the tasks are duplicates; false otherwise.
     */
    public boolean isDuplicate(Task other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return description.equalsIgnoreCase(other.description);
    }

    /**
     * Returns this task in the format used by task-list persistence.
     *
     * @return serialised task data.
     */
    public String toFileFormat() {
        return "T | " + getDoneFlag() + " | " + description;
    }

    /**
     * Returns the completion flag expected by the storage format.
     *
     * @return {@code 1} when the task is done; otherwise, {@code 0}.
     */
    protected int getDoneFlag() {
        return isDone ? 1 : 0;
    }

    /**
     * Returns the associated date and time of this task, if any.
     *
     * @return an {@link Optional} containing the date-time, or empty if undated.
     */
    public Optional<LocalDateTime> getDateTime() {
        return Optional.empty();
    }

    /**
     * Returns the user-entered task description.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the user-facing representation of this event task.
     *
     * @return formatted task description.
     */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }
}
