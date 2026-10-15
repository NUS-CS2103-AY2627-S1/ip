package lebron;

/**
 * Represents a general task in the chatbot.
 * Stores the task description, completion status, and task type.
 */
public class Task {

    protected String description;
    protected boolean isDone;
    protected TaskType type;

    /**
     * Creates a new task with the given description.
     * The task is initially marked as not done.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Returns the status icon representing whether the task is completed.
     *
     * @return "X" if the task is done, or a blank space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the string representation of the task.
     *
     * @return formatted task description and completion status
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
