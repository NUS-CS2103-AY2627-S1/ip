package nabmak.task;

/**
 * Represents a todo task with a description.
 */
public class ToDo extends Task {
    /**
     * Creates a todo task with a description.
     *
     * @param desc description of task
     */
    public ToDo(String desc) {
        super(desc);
    }

    /**
     * Converts task to user readable string format.
     *
     * @return todo task in string format
     */
    @Override
    public String toString() {
        return "[To Do]" + super.toString();
    }
}
