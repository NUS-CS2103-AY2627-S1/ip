package ev.task;

/**
 * Represents a task in E.V.'s registry, holding a description and a done status.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a task with the given description, initially not done.
     *
     * @param description the description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon shown for this task's done status.
     *
     * @return "X" if the task is done, a blank otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the description this task was created with.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns this task encoded for storage in the data file.
     *
     * @return the done flag and description, separated by a pipe.
     */
    public String toFileString() {
        return (isDone ? "1" : "0") + " | " + description;
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

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
