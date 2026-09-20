package bonbon.tasklist;

/**
 * ToDo is a type of task
 */
public class ToDo extends Task {

    /**
     * Constructs a ToDo task with the specified name.
     *
     * @param name The name or description of the todo task.
     */
    public ToDo(String name) {
        super(name);
    }

    /**
     * Returns the string representation of the todo task, including its task type tag.
     *
     * @return The formatted string representation of the todo task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns the string representation of the todo task formatted for storage persistence.
     *
     * @return The formatted string suitable for saving to a file.
     */
    @Override
    public String toFileFormat() {
        return String.format("T | %s", super.toFileFormat());
    }
}