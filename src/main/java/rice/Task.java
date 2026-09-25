package rice;
import java.time.LocalDate;

/**
 * Represents a task with a description and completion state.
 */
public abstract class Task {
    private String task;
    private boolean isDone = false;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param task the task description
     */
    public Task(String task) {
        this.task = task;
    }

    /**
     * Marks this task as complete.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getTask() {
        return this.task;
    }

    /**
     * Checks whether the task has been marked as done.
     *
     * @return true if the task is done and false otherwise
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns a user-friendly string representation of the task.
     *
     * @return the formatted task string
     */
    @Override
    public String toString() {
        return this.completionMarker() + " " + this.task;
    }

    /**
     * Returns the status marker for the task.
     *
     * @return "[X]" if the task is done, otherwise "[ ]"
     */
    public String completionMarker() {
        return this.isDone() ? "[X]" : "[ ]";
    }

    /**
     * Returns the date that determines the ordering of the task.
     *
     * @return the task's relevant due date or a sentinel value for tasks without dates
     */
    public abstract LocalDate getDeadlineDate();
}
