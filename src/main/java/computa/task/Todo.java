package computa.task;

/**
 * A task without a date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param description text describing the task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo in the format used by the task data file.
     *
     * @return the todo type, status, and description.
     */
    @Override
    public String toFileFormat() {
        return "T" + super.toFileFormat();
    }
}
