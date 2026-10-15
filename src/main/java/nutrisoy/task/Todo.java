package nutrisoy.task;

/**
 * Represents a task without a date or time requirement.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the supplied description.
     *
     * @param description description of the task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo in the storage-file format.
     *
     * @return storage-file representation of this todo
     */
    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }

    /**
     * Returns a display representation of this todo.
     *
     * @return display representation of this todo
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
