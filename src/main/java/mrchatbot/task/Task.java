package mrchatbot.task;

/**
 * Represents a task in the chatbot's task list.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a task that is not marked as done yet.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns X if the task is done, or a blank space otherwise.
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
     * Marks this task as not done yet.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns true if this task is marked as done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task in the display format used by the chatbot.
     */
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

}
