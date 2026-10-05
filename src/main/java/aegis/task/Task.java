package aegis.task;

import java.time.LocalDate;

/**
 * Represents a task in the Aegis task list.
 */
public abstract class Task {

    protected String description;
    protected boolean isDone;

    /**
     * Constructs a task.
     *
     * @param description Description of the task.
     * @param isDone Whether the task has been marked as done.
     */
    public Task(String description, boolean isDone) {
        assert description != null : "Task description should not be null";
        assert !description.isBlank() : "Task description should not be blank";
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Returns the LocalDate of the task if present, otherwise MAX.
     */
    public LocalDate getDate() {
        return LocalDate.MAX;
    }

    /**
     * Returns the display icon for the task completion status.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Marks the task as not done.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Marks the task as done.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Returns the letter icon used to identify this task's type.
     */
    public abstract String getTypeIcon();


    /**
     * Returns the display form of this task.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns the file storage format of this task.
     */
    public abstract String getFileSaveFormat();

    public String getDescription() {
        return description;
    }

    /**
     * Checks whether another task has the same user-visible task details.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equals(other.description);
    }

}
