package duke.task;

/**
 * Represents a generic task with a name and completion status.
 */
public class Task {
    private final String name;
    private boolean isDone;

    /**
     * Constructs a new Task with the given name.
     *
     * @param name The description of the task.
     */
    public Task(String name) {
        this.name = name;
        this.isDone = false;
    }

    /**
     * Returns whether the task is marked as done.
     *
     * @return True if the task is done, false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the description of the task.
     *
     * @return The task description.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the status icon for the task.
     *
     * @return "X" if done, " " otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Formats the Task for saving to a file.
     *
     * @return A pipe-separated string representing the Task's state.
     */
    public String toFileFormat() {
        return getTaskIcon() + " | " + (isDone ? "1" : "0") + " | " + name;
    }

    /**
     * Returns the task type icon.
     *
     * @return A string representing the task type.
     */
    public String getTaskIcon() {
        return " ";
    }

    /**
     * Marks the task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Creates and returns a copy of this task.
     *
     * @return A copy of this task.
     */
    public Task copy() {
        Task copy = new Task(this.name);
        if (this.isDone) {
            copy.markAsDone();
        }
        return copy;
    }

    /**
     * Returns the description of the task.
     *
     * @return The task description string.
     */
    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Task otherTask = (Task) other;
        return name.equals(otherTask.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
