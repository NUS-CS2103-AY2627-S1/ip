package walter.task;

/**
 * Represents a task description together with its completion status.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a new task that is initially not done.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        assert description != null && !description.isBlank()
                : "Task description should be validated before task creation";
        this.description = description;
        isDone = false;
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
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task description for persistence.
     *
     * @return Description of this task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Reports whether this task has been completed.
     *
     * @return {@code true} if this task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the icon representing this task's completion status.
     *
     * @return {@code X} if done, or a blank space if not done.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task's status marker followed by its description.
     *
     * @return Display form such as {@code [X] read book}.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
