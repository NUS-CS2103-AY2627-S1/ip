package task;

import michael.MichaelException;

/**
 * Represents one task that can be marked as done or not done.
 */
public class Task {
    /** The text describing this task. */
    private final String description;

    /** Whether this task has been completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description and type.
     *
     * @param description the text describing the task
     * @param taskType the human-readable type used in validation messages
     * @throws MichaelException if the task description is empty
     */
    protected Task(String description, String taskType) throws MichaelException {
        assert taskType != null && !taskType.isBlank() : "Task type is required for validation messages";
        if (description == null || description.isBlank()) {
            String article = taskType.equals("event") ? "an" : "a";
            throw new MichaelException(" Oh No! The description of " + article + " "
                    + taskType + " is empty. Please add a description after the task type");
        }
        this.description = description;
        this.isDone = false;
        assert this.description != null && !this.description.isBlank()
                : "A successfully constructed task must have a description";
    }

    /**
     * Returns the task description.
     *
     * @return the text describing this task
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the icon used to display this task's completion status.
     *
     * @return {@code "X"} for a completed task, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not completed. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task's status and description.
     *
     * @return the text used to display a basic task
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
