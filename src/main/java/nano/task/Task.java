package nano.task;

import nano.NanoException;

/**
 * Represents a task that can be added to the task list.
 */
public class Task {
    private String description;
    private boolean isDone;
    private Priority priority;

    /**
     * Represents a task that can be marked as done or not done.
     */
    public Task(String description) throws NanoException {
        this.description = description.trim();
        this.isDone = false;
        this.priority = Priority.NORMAL;

        if (this.description.isEmpty()) {
            throw new NanoException("The description cannot be empty.");
        }
    }

    /**
     * Marks this task as done.
     */
    public void markDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markUndone() {
        isDone = false;
    }

    public String getDescription() {
        return description;
    }

    public String getStatusIcon() {
        return isDone ? "[X]" : "[ ]";
    }

    /**
     * Returns whether this task is completed.
     *
     * @return true if the task is done, or false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getPriorityText() {
        return priority.toString().toLowerCase();
    }

    /**
     * Returns this task in the format used for saving it to the data file.
     *
     * @return storage representation of this task.
     */
    public String toStorageString() {
        assert !description.isEmpty();
        return "T | " + (isDone ? "1" : "0")
                + " | " + getPriorityText()
                + " | " + description;
    }

    @Override
    public String toString() {
        return getStatusIcon() + " " + getDescription()
                + " (priority: " + getPriorityText() + ")";
    }
}
