package shai.task;

/**
 * Represents a task without an attached date or time.
 */
public class ToDo extends Task {
    /**
     * Creates a ToDo that is initially not done.
     *
     * @param description the text describing the ToDo
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns the ToDo with its type and completion status.
     *
     * @return the formatted ToDo description
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
