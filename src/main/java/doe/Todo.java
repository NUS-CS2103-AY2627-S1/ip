package doe;


/**
 * Represents a task that must be completed by a stated deadline.
 * The deadline wording is kept as entered by the user.
 */
public class Todo extends Task {

    /**
     * Creates a todo task without any deadline.
     *
     * @param taskName Description of the todo task.
     */
    public Todo(String taskName) {
        super(taskName);
    }

    /**
     * Returns a string formatted for saving the todo task to a file.
     *
     * @return File-formatted string representation of the todo task.
     */
    @Override
    public String toFileFormat() {
        return "TODO|" + super.toFileFormat();
    }

    /**
     * Returns this todo task in the format used when displaying a task list.
     *
     * @return String representation of the todo task.
     */
    @Override
    public String toString() {
        return "[t][" + getStatusIcon() + "] " + taskName;
    }
}
