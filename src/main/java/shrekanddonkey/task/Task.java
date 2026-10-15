package shrekanddonkey.task;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    /** Text describing the task. */
    private String description;
    /** Whether the task has been completed. */
    private boolean isDone;

    /**
     * Creates a task that is initially not done.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        assert description != null : "Task description cannot be null";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the character used to display the task's completion status.
     *
     * @return {@code X} when done, or a space when not done.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
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
     * Returns the description of this task.
     *
     * @return description text.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the shared status and description portion of a task.
     *
     * @return task text in the format shown by the chatbot.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
