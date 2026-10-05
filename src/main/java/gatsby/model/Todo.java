package gatsby.model;

/** A task with a description and no additional date-time fields. */
public class Todo extends Task {

    /**
     * Creates an unfinished todo task.
     *
     * @param description the task description
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the todo with its type marker and completion status.
     *
     * @return the human-readable todo representation
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns the todo in the pipe-separated format used by storage.
     *
     * @return the saved todo representation
     */
    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }
}
