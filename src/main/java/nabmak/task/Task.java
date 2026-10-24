package nabmak.task;

/**
 * Represents a general task.
 */
public class Task {
    private String desc;
    private boolean isDone;

    /**
     * Creates an undone task with a specified description.
     *
     * @param desc description of task
     */
    public Task(String desc) {
        this.desc = desc;
        this.isDone = false;
    }

    /**
     * Marks a task as complete
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Marks a task as incomplete
     */
    public void markNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the status on whether a task is complete or incomplete.
     *
     * @return status of task with X indicating complete
     */
    public String getStatus() {
        return isDone ? "[X]" : "[ ]";
    }

    /**
     * Returns the description of a task.
     *
     * @return description of task
     */
    public String getDesc() {
        return this.desc;
    }

    /**
     * Returns string representation of task with status and description.
     *
     * @return string representation of task
     */
    @Override
    public String toString() {
        return this.getStatus() + " " + this.desc;
    }
}
