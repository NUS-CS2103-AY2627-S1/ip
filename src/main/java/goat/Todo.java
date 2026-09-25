package goat;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description description of the todo
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo in the data-file format.
     *
     * @return serialized todo data
     */
    @Override
    public String toFileString() {
        return formatFileData("T");
    }

    /**
     * Returns the todo with its type and completion status.
     *
     * @return formatted todo
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
