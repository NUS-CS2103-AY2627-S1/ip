package duke.task;

/**
 * Represents one task in the chatbot's task list.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a new task that starts as not done.
     */
    public Task(String description) {
        assert description != null && !description.isBlank() : "Task description should not be blank";
        this.description = description;
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
     * Returns the icon used to show whether this task is done.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns true if this task has been marked as done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task in the format shown to the user.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
