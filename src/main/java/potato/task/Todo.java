package potato.task;

/**
 * Represents a basic todo task without any date or time constraints.
 */
public class Todo extends Task {

    /**
     * Constructs a {@code Todo} task with the specified description.
     *
     * @param description Textual description of the todo task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the string representation of the todo task.
     *
     * @return Formatted todo display string.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns the todo task in the plain-text format used for file storage.
     *
     * @return Formatted save file line for this todo.
     */
    public String toFileFormat() {
        String isDoneFlag = getStatusIcon().equals("X") ? "1" : "0";
        return "T | " + isDoneFlag + " | " + getDescription();
    }
}
