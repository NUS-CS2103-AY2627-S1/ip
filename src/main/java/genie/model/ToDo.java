package genie.model;

/** Represents a task without any specific date or time attached. */
public class ToDo extends Task {

    /**
     * Initializes a new ToDo task.
     *
     * @param description Description of the task.
     */
    public ToDo(String description) {
        super(description);
    }

    /** Returns the todo in the format used for file storage. */
    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }

    /** Returns the todo with its type and completion status. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
