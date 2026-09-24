package hampster.task;

/**
 * Represents a simple task without a deadline or event time.
 */
public class ToDo extends Task {

    /**
     * Creates a to-do task with the specified completion status.
     *
     * @param done whether the task is completed
     * @param description the task description
     */
    public ToDo(boolean done, String description) {
        super(done, description);
    }

    /** Creates a to-do task with an optional tag. */
    public ToDo(boolean done, String description, String tag) {
        super(done, description, tag);
    }

    /**
     * Creates an incomplete to-do task.
     *
     * @param description the task description
     */
    public ToDo(String description) {
        this(false, description);
    }

    /**
     * Converts this task into its pipe-separated storage format.
     *
     * @return the serialized to-do task
     */
    @Override
    public String saveString() {
        return String.format(
            "T|%s|%s|%s",
                this.done ? "1" : "0",
            this.description,
            this.tag
        );
    }

    /**
     * Returns a user-readable representation of this to-do task.
     *
     * @return the task type, completion status, and description
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
