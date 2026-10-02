package ari.task;

/**
 * Defines state and behavior shared by all task types.
 */
public abstract class Task {
    protected final String description;
    protected boolean isDone;
    protected final String type;

    /**
     * Creates an incomplete task with the specified description and type.
     *
     * @param description Description of the task.
     * @param type Storage code identifying the task type.
     */
    protected Task(String description, String type) {
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /**
     * Returns an independent task with the same fields and completion state.
     *
     * @return Independent copy suitable for a staged change.
     */
    public abstract Task copy();

    /**
     * Toggles the completion state of this task.
     */
    public void changeState() {
        this.isDone = !this.isDone;
    }

    /**
     * Marks this task as completed.
     */
    public void markTask() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmarkTask() {
        this.isDone = false;
    }

    /**
     * Returns this task as a storage record.
     *
     * @return Storage representation of this task.
     */
    public String toDataString() {
        String status = this.isDone
                ? "1"
                : "0";

        return String.format(
                "%s | %s | %s",
                this.type,
                status,
                this.description
        );
    }

    /**
     * Returns this task in a user-readable format.
     *
     * @return User-readable representation of this task.
     */
    @Override
    public String toString() {
        String box;

        if (this.isDone) {
            box = "[X]";
        } else {
            box = "[ ]";
        }

        return String.format("[%s]%s %s", this.type, box, this.description);
    }
}
