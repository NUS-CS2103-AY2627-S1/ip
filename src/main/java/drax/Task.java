package drax;

/**
 * Base representation of a task and its completion state.
 */
public class Task {
    protected String task;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the supplied description.
     *
     * @param task description of the task.
     */
    public Task(String task) {
        this.task = task;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getTask() {
        return this.task;
    }

    /**
     * Checks whether this task has been completed.
     *
     * @return true if the task is completed
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns X for a completed task and a blank marker otherwise.
     *
     * @return completion marker used in task display output
     */
    public String getStatusIcon() {
        return (this.isDone ? "X" : " ");
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmarkAsDone() {
        this.isDone = false;
    }
}
