package remy.task;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents a task with a description and completion state.
 *
 * @author LEE-wz
 */
public class Task {
    /** Description supplied for this task. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description supplied by the user.
     */
    public Task(String description) {
        assert description != null : "Task description must not be null";
        assert !description.isBlank() : "Task description must not be blank";

        this.description = description;
        this.isDone = false;
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
     * Returns whether another task has the same type and identifying details.
     *
     * <p>Completion status is deliberately ignored because marking a task does not create a new task.</p>
     *
     * @param otherTask task to compare with
     * @return true when both tasks have the same type and details
     */
    public boolean hasSameDetailsAs(Task otherTask) {
        return otherTask != null
                && getClass().equals(otherTask.getClass())
                && description.equals(otherTask.description);
    }

    /**
     * Returns the date-time used to sort this task chronologically, if one exists.
     *
     * @return an empty value because a general task has no chronological endpoint
     */
    Optional<LocalDateTime> getChronologicalDateTime() {
        return Optional.empty();
    }

    /**
     * Returns this task's completion status icon.
     *
     * @return 'X' if this task is done
     *         ' ' if this task is not done
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as undone. */
    public void markAsUndone() {
        this.isDone = false;
    }

    /**
     * Returns this task's display representation.
     *
     * @return A string containing the status icon and description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
