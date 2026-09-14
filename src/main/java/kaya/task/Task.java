package kaya.task;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a task with the given description and an initial not-done status.
     *
     * @param description the description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
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
     * Returns the task description for saving to disk.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if the task is completed
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Creates an independent copy for restoring the task after a failed save.
     *
     * @return a copy with the same description and completion status
     */
    public Task copy() {
        return copyStatusTo(new Task(description));
    }

    /**
     * Copies completion status onto a newly created task of the same type.
     *
     * @param copy the task whose description and dates have already been copied
     * @return the copy with this task's completion status
     */
    protected Task copyStatusTo(Task copy) {
        if (isDone) {
            copy.markAsDone();
        }
        return copy;
    }

    /**
     * Returns an icon representing the completion status.
     *
     * @return {@code X} when done, or a space when not done
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task in the format used in chatbot responses.
     *
     * @return the status icon followed by the task description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
